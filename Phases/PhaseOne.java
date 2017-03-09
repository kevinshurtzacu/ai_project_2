import java.util.List;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;

public class PhaseOne {
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

            if (args[index].equals("--help") || args[index].equals("-h"))
                help = true;
        }

        // configure input source
        KnapsackGenome.setInput(input);

        // display help
        if (help) {
            PhaseLib.printHelp(System.out, populationSize, numOffspring, radioactivity,
                               generationCap, numConverge, punctuation);
        }
        else {
            // read genes
            KnapsackGenome.readData();

            // create genome
            PhaseLib.generationNum.generationCap = generationCap;
            Genome mostFit = PhaseLib.mostFit(KnapsackGenome.builder, populationSize, numOffspring,
                                              radioactivity, social, oneChild, decider, numConverge,
                                              punctuation);

            // display genome information
            System.out.println("Bit String: " + mostFit.genome);

            System.out.print("\nName:     ");
            for (KnapsackGene gene : ((KnapsackGene[])Genome.genes))
                System.out.format("%4s", gene.name);

            System.out.print("\nValue:    ");
            for (KnapsackGene gene : ((KnapsackGene[])Genome.genes))
                System.out.format("%4d", gene.value);

            System.out.print("\nCost:     ");
            for (KnapsackGene gene : ((KnapsackGene[])Genome.genes))
                System.out.format("%4d", gene.cost);

            System.out.print("\nSelected: ");
            for (int index = 0; index < Genome.genes.length; ++index) {
                System.out.print(mostFit.genome.get(index) ? "   ^" : "    ");
            }

            // display value and capacity
            System.out.println("\n\nTotal Value: " + mostFit.getValue());
        }
    }
}
