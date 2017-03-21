import java.util.EnumSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.io.InputStream;

// represents a genome for the knapsack problem
public class ScheduleGenome extends Genome {
    private static String studentName;
    private static String[] required;

    // generates a knapsack genome
    public static GenomeBuilder builder = new GenomeBuilder() {
        public Genome build() {
            return new ScheduleGenome();
        }

        public Genome build(int length) {
            return new ScheduleGenome(length);
        }

        public Genome build(Genome src) {
            return new ScheduleGenome(src);
        }

        public Genome build(Genome mother, Genome father) {
            return new ScheduleGenome(mother, father);
        }

        public Genome build(Genome mother, Genome father, EnumSet<Property> properties) {
            return new ScheduleGenome(mother, father, properties);
        }

        public Genome build(Genome mother, Genome father, int split) {
            return new ScheduleGenome(mother, father, split);
        }

        public Genome build(Genome mother, Genome father, int split, EnumSet<Property> properties) {
            return new ScheduleGenome(mother, father, split, properties);
        }
    };

    // create a random genome
    private ScheduleGenome() {
        super();
    }

    // create a random genome of size 'length'
    private ScheduleGenome(int length) {
        super(length);
    }

    // clone a genome
    private ScheduleGenome(Genome src) {
        super(src);
    }

    // create a genome from a mother and a father
    private ScheduleGenome(Genome mother, Genome father) {
        super(mother, father);
    }

    // create a genome from a mother and a father with flags
    private ScheduleGenome(Genome mother, Genome father, EnumSet<Property> properties) {
        super(mother, father, properties);
    }

    // create a genome from a mother and a father with a designated split point
    private ScheduleGenome(Genome mother, Genome father, int split) {
        super(mother, father, split);
    }

    // create a genome from a mother and a father with a designated split point and flags
    private ScheduleGenome(Genome mother, Genome father, int split, EnumSet<Property> properties) {
        super(mother, father, split, properties);
    }

    // set required courses
    public static void setStudent(String line) {
        // list of required courses
        List<String> courses = new ArrayList<String>();

        // read student name and required courses
        String[] tokens = line.split(",");
        studentName = tokens[0];

        for (int courseIndex = 1; courseIndex < tokens.length; ++courseIndex)
            courses.add(tokens[courseIndex]);

        // set required courses
        required = courses.toArray(new String[courses.size()]);
    }

    // get student name
    public static String getStudentName() {
        return studentName;
    }

    // get required courses
    public static String[] getRequired() {
        return required;
    }

    // helper function, read gene data into List
    private static Gene readSections(String line) {
        // read name, start time, end time, days, season, year
        String[] tokens = line.split(",");

        String courseName = tokens[0];
        int startTime = Integer.parseInt(tokens[1]);
        int endTime = Integer.parseInt(tokens[2]);
        String days = tokens[3];
        String season = tokens[4];
        int year = Integer.parseInt(tokens[5]);

        // return new gene
        return new ScheduleGene(courseName, startTime, endTime, days, season, year);
    }

    // read in the data
    public static void readData() {
        // list of genes
        List<Gene> genes = new ArrayList<Gene>();

        // create list of required and available courses
        boolean requiredList = false;

        while (Genome.scan.hasNextLine()) {
            String line = Genome.scan.nextLine();

            // ensure the line is well-formed
            if (requiredList && line.replace(",", "").length() == line.length() - 5)
                genes.add(readSections(line));

            // toggle parsing
            if (line.equals("Section List Begin"))
                requiredList = true;

            if (line.equals("Section List End"))
                requiredList = false;
        }

        // set the genes
        Genome.genes = genes.toArray(new ScheduleGene[genes.size()]);
    }

    // determine the value of the genome
    @Override
    public int getValue() {
        int value = 0;      // total number of scheduled classes, 10 pts per class
        int conflicts = 0;  // total number of class conflicts, 10 pts per conflict
        int wastes = 0;     // total number of unnecessary classes, 100 pts per waste
        int delays = 0;     // penalties for taking a course later rather than sooner

        // current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // total the value of the genome
        for (int firstBitIndex = genome.nextSetBit(0);
                firstBitIndex != -1;
                firstBitIndex = genome.nextSetBit(firstBitIndex + 1)) {

            // check if section was required, add value or add waste
            ScheduleGene section = (ScheduleGene)Genome.genes[firstBitIndex];
            boolean inRequired = false;

            for (String course : required) {
                if (section.name.equals(course))
                    inRequired = true;
            }

            if (inRequired) {
                // add 5 pts to value if this course was required
                value += 10;

                // penalize taking classes later rather than sooner
                delays += section.year - currentYear;
            }
            else
                wastes += 100; // add 100 pts to wastes if it was not

            // check for conflicts
            for (int secondBitIndex = genome.nextSetBit(0);
                    secondBitIndex != -1;
                    secondBitIndex = genome.nextSetBit(secondBitIndex + 1)) {
                // create potential conflicting sections
                ScheduleGene firstSection  = section;   // already casted in outer scope
                ScheduleGene secondSection = (ScheduleGene)Genome.genes[secondBitIndex];

                // check for conflicts between the sections
                if (firstBitIndex != secondBitIndex &&
                        (firstSection.conflicts(secondSection) || firstSection.isRedundant(secondSection)))
                    conflicts += 10;
            }
        }

        // return value adjusted for conflicts and waste
        return value - conflicts - wastes - delays;
    }
}
