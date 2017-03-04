import java.util.EnumSet;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;

// represents a genome for the knapsack problem
public class KnapsackGenome extends Genome {
    private static int capacity;
    
    // generates a knapsack genome
    public static GenomeBuilder builder = new GenomeBuilder() {
        public Genome build() {
            return new KnapsackGenome();
        }
        
        public Genome build(int length) {
            return new KnapsackGenome(length);
        }
        
        public Genome build(Genome src) {
            return new KnapsackGenome(src);
        }
        
        public Genome build(Genome mother, Genome father) {
            return new KnapsackGenome(mother, father);
        }
        
        public Genome build(Genome mother, Genome father, EnumSet<Property> properties) {
            return new KnapsackGenome(mother, father, properties);
        }
        
        public Genome build(Genome mother, Genome father, int split) {
            return new KnapsackGenome(mother, father, split);
        }
        
        public Genome build(Genome mother, Genome father, int split, EnumSet<Property> properties) {
            return new KnapsackGenome(mother, father, split, properties);
        }
    };
    
    // create a random genome
    private KnapsackGenome() {
        super();
    }
    
    // create a random genome of size 'length'
    private KnapsackGenome(int length) {
        super(length);
    }
    
    // clone a genome
    private KnapsackGenome(Genome src) {
        super(src);
    }
    
    // create a genome from a mother and a father
    private KnapsackGenome(Genome mother, Genome father) {
        super(mother, father);
    }
    
    // create a genome from a mother and a father with flags
    private KnapsackGenome(Genome mother, Genome father, EnumSet<Property> properties) {
        super(mother, father, properties);
    }
    
    // create a genome from a mother and a father with a designated split point
    private KnapsackGenome(Genome mother, Genome father, int split) {
        super(mother, father, split);
    }
    
    // create a genome from a mother and a father with a designated split point and flags
    private KnapsackGenome(Genome mother, Genome father, int split, EnumSet<Property> properties) {
        super(mother, father, split, properties);
    }
     
    // helper function, read capacity data
    private static int readCapacity() {
        // determine knapsack capacity
        int capacity = Genome.scan.nextInt();
        Genome.scan.nextLine();

        return capacity;
    }
    
    // helper function, read gene data into List
    private static List<Gene> readItems() {
        // read all genes
        List<Gene> genes = new ArrayList<Gene>();

        while (Genome.scan.hasNextLine()) {
            // read name, cost, value
            String line = Genome.scan.nextLine();
            String[] tokens = line.split(",");

            String name = tokens[0];
            int cost = Integer.parseInt(tokens[1]);
            int value = Integer.parseInt(tokens[2]);

            genes.add(new KnapsackGene(name, cost, value));
        }

        return genes;
    }

    // read in the data
    public static void readData() {
        // define population and capacity
        capacity = readCapacity();
        
        List<Gene> genes = readItems();
        Genome.genes = genes.toArray(new KnapsackGene[genes.size()]);
    }
    
    // determine the value of the genome
    public int getValue() {
        int value = 0;
        int cost = 0;
        
        // total the value of the genome
        for (int bitIndex = genome.nextSetBit(0); 
                bitIndex != -1; 
                bitIndex = genome.nextSetBit(bitIndex + 1)) {
            value += ((KnapsackGene)Genome.genes[bitIndex]).value;
            cost  += ((KnapsackGene)Genome.genes[bitIndex]).cost;
        }
        
        // if the cost exceeds capacity the genome loses its value
        if (cost > capacity)
            value = (capacity - cost);
        
        return value;
    }
}