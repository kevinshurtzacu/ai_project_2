import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;

public class PhaseTwo {
    // display all relevant information about genome
    public static void displayAll(Genome mostFit) {
        // display genome information
        System.out.println("Bit String: " + mostFit.genome);
        System.out.println("\nRecommended courses for " + ScheduleGenome.getStudentName() + ":");
        for (int bitIndex = mostFit.genome.nextSetBit(0);
                bitIndex != -1;
                bitIndex = mostFit.genome.nextSetBit(bitIndex + 1)) {
            ScheduleGene gene = (ScheduleGene)Genome.genes[bitIndex];
            System.out.format("%-40s", gene.name);
            System.out.format("%4d - %4d %-8s", gene.startTime, gene.endTime, gene.days);
            System.out.format("%-9s %4d\n", gene.season, gene.year);
        }

        // display value and capacity
        System.out.println("\n\nTotal Value: " + mostFit.getValue());
    }

    // display only final result
    public static void displayValue(Genome mostFit) {
        System.out.print(mostFit.getValue());
    }

    // display only time
    public static void displayTime(long avgTime) {
        System.out.print(avgTime);
    }

    public static void main(String... args) {
        // default variable options
        int populationSize = 100;
        int numOffspring = 7;
        float radioactivity = .005f;
        boolean social = false;
        boolean oneChild = false;
        int numConverge = 3;
        float punctuation = .20f;
        int generationCap = 100;
        InputStream input = System.in;
        Decider decider = PhaseLib.convergeValue;
        boolean time = false;
        int numTrials = 1;
        boolean bare = false;
        boolean help = false;

        // process command line arguments
        for (int index = 0; index < args.length; ++index) {
            if (args[index].equals("--population-size") || args[index].equals("-p"))
                populationSize = Integer.parseInt(args[++index]);

            if (args[index].equals("--children-num") || args[index].equals("-c"))
                numOffspring = Integer.parseInt(args[++index]);

            if (args[index].equals("--radioactivity") || args[index].equals("-r"))
                radioactivity = Float.parseFloat(args[++index]);

            if (args[index].equals("--social") || args[index].equals("-s"))
                social = true;

            if (args[index].equals("--one-child") || args[index].equals("-o"))
                oneChild = true;

            if (args[index].equals("--converge-num") || args[index].equals("-v"))
                numConverge = Integer.parseInt(args[++index]);

            if (args[index].equals("--punctuation") || args[index].equals("u"))
                punctuation = Float.parseFloat(args[++index]);

            if (args[index].equals("--generation-cap") || args[index].equals("-g"))
                generationCap = Integer.parseInt(args[++index]);

            if (args[index].equals("--file-input") || args[index].equals("-f")) {
                try {
                    input = Files.newInputStream(Paths.get(args[++index]), StandardOpenOption.READ);
                }
                catch (IOException ex) {
                    System.err.println(ex);
                    System.exit(-1);
                }
            }
            if (args[index].equals("--decide") || args[index].equals("-d")) {
                String selection = args[++index];

                if (selection.equals(PhaseLib.convergeValue.name))
                    decider = PhaseLib.convergeValue;

                if (selection.equals(PhaseLib.convergeGenome.name))
                    decider = PhaseLib.convergeGenome;

                if (selection.equals(PhaseLib.generationNum.name))
                    decider = PhaseLib.generationNum;
            }

            if (args[index].equals("--time") || args[index].equals("-t"))
                time = true;

            if (args[index].equals("--num-trials") || args[index].equals("-n"))
                numTrials = Integer.parseInt(args[++index]);

            if (args[index].equals("--bare") || args[index].equals("-b"))
                bare = true;

            if (args[index].equals("--help") || args[index].equals("-h"))
                help = true;
        }

        // configure input source
        ScheduleGenome.setInput(input);

        // display help
        if (help) {
            PhaseLib.printHelp(System.out, populationSize, numOffspring, radioactivity,
                               generationCap, numConverge, punctuation);
        }
        else {
            // read genes
            ScheduleGenome.readData();

            // read students
            Scanner studentScan = null;

            try {
                studentScan = new Scanner(Files.newInputStream(Paths.get("../Schedule Input Files/A1_Students.txt"), StandardOpenOption.READ));
            }
            catch (IOException ex) {
                System.err.println(ex);
                System.exit(-1);
            }

            String student = studentScan.nextLine();
            ScheduleGenome.setStudent(student);

            // create genome
            PhaseLib.generationNum.generationCap = generationCap;
            Genome mostFit = null;

            // conduct number of specified trials
            long startTime = 0;
            long endTime = 0;
            long total = 0;

            for (int trial = 0; trial < numTrials; ++trial) {
                // conduct trial
                startTime = System.nanoTime();
                mostFit = PhaseLib.mostFit(ScheduleGenome.builder, populationSize, numOffspring,
                                              radioactivity, social, oneChild, decider, numConverge,
                                              punctuation);
                endTime = System.nanoTime();

                // add to total
                total += (endTime - startTime);
            }

            long avgTime = Math.round((double)total / numTrials);

            // display results
            if (time)
                displayTime(avgTime);
            else if (bare)
                displayValue(mostFit);
            else
                displayAll(mostFit);
        }
    }
}
