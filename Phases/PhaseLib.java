import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.InputStream;

public class PhaseLib {
    // data input
    private static Scanner scan;

    // configure input source
    public static void setInput(InputStream in) {
        scan = new Scanner(in);
    }

    // print usage
    private static void printUsage(PrintStream out) {
        out.println("Usage: [std input] | java PhaseOne [options]");
    }

    // print flags
    private static void printFlags(PrintStream out) {
        out.println("\nOptions:");
        out.println(" -p, --population-size [integer]   set the number of parent genomes in population\n");

        out.println(" -c, --children-num [integer]      set the number of children generated by each member\n" +
                    "                                   of the population\n");

        out.println(" -r, --radioactivity [float]       set the probability of a random mutation occuring\n" +
                    "                                   during reproduction\n");

        out.println(" -s, --social                      combine genomes at random instead of combining genomes of\n" +
                    "                                   comparable fitness, father is chosen at random\n");

        out.println(" -o, --one-child                   create only one child after each generation, mother is\n" +
                    "                                   always the most fit, father is determined by 'social' flag\n");

        out.println(" -v, --converge-num [integer]      after a potentally optimal genome is found, repeat search\n" +
                    "                                   until the same result is unchallenged 'num-converge' times\n");

        out.println(" -u, --punctuation [float]         the mutation rate (or \"radioactivity\") used when\n" +
                    "                                   mutating the seed genomes between \"runs\"\n");

        out.println(" -g, --generation-cap [integer]    set the number generations before top-ranking genome\n" +
                    "                                   is selected, works only when 'gen-num' is used\n");

        out.println(" -d  --decide ['gen-num' | 'conv-gen' | 'conv-value']\n" +
                    "                                   set the decision mechanism for determining an optimal\n" +
                    "                                   genome\n");

        out.println(" -f, --file-input [file name]      select an input file instead of using standard input\n");

        out.println(" -t, --time                        time the system for a given input in nanoseconds\n");

        out.println(" -n, --num-trials [integer]        used in conjunction with the time switch, specifies number\n" +
                    "                                   of trials to use when creating average runtime\n");

        out.println(" -b, --bare                        display only the bare value produced by the algorithm\n");

        out.println(" -h, --help                        display help information");
    }

    // print defaults
    private static void printDefaults(PrintStream out, int populationSize, int numOffspring,
                                      float radioactivity, int generationCap, int numConverge,
                                      float punctuation) {
        out.println("\nDefaults:");
        out.format(" java PhaseOne -p %d -c %d -r %.3f -g %d -d 'conv-value' -v %d -u %f -n 1\n",
                   populationSize, numOffspring, radioactivity, generationCap, numConverge, punctuation);
    }

    // print examples
    private static void printExamples(PrintStream out) {
        String baseStub = "cat inputfile.csv | java PhaseOne";

        out.println("\nExamples:");
        out.format(" %s                 process inputfile.csv with defaults\n", baseStub);
        out.format(" %s -p 100          maintains population of 100 genomes\n", baseStub);
        out.format(" %s -c 20           population members generate 20 children each\n", baseStub);
        out.format(" %s -r .50          50%% chance of a random mutation per \"gene\"\n", baseStub);
        out.format(" %s -s              combines genomes at random\n", baseStub);
        out.format(" %s -g 100          returns the best-fit genome if constant for 100 generations\n", baseStub);
        out.format(" %s -d 'gen-num'    returns genome when best-fit is constant for N generations\n", baseStub);
        out.format(" %s -f input.csv    reads input from the file entitled \"input.csv\"\n", baseStub);
        out.format(" %s -c              creates only one child per generation\n", baseStub);
    }

    // print help (no defaults)
    public static void printHelp(PrintStream out) {
        printUsage(out);
        printFlags(out);
        printExamples(out);
    }

    // print help (with defaults)
    public static void printHelp(PrintStream out, int populationSize, int numOffspring, float radioactivity,
                                 int generationCap, int numConverge, float punctuation) {
        printUsage(out);
        printDefaults(out, generationCap, populationSize, radioactivity, numOffspring, numConverge, punctuation);
        printFlags(out);
        printExamples(out);
    }

    // returns true after N generations of the same best-fit candidate
    public static Decider generationNum = new Decider() {
        // define decision-type name
        public String name = "gen-num";

        @Override
        public boolean decide(Genome[] habitat) {
            // track number of recurrences
            if (habitat[0].equals(lastGeneration))
                same += 1;
            else
                same = 1;

            // remember last generation
            lastGeneration = habitat[0];

            // return true after the Nth generation
            if (same == generationCap)
                return true;

            return false;
        }
    };

    // returns true after the convergence of a genome
    public static Decider convergeGenome = new Decider() {
        // define decision-type name
        public String name = "conv-gen";

        @Override
        public boolean decide(Genome[] habitat) {
            for (int index = 0; index < populationSize - 1; ++index) {
                if (!habitat[index].equals(habitat[index + 1]))
                    return false;
            }

            return true;
        }
    };

    // returns true after the convergence of a best-fit value
    public static Decider convergeValue = new Decider() {
        // define decision-type name
        public String name = "conv-value";

        @Override
        public boolean decide(Genome[] habitat) {
            for (int index = 0; index < populationSize - 1; ++index) {
                if (habitat[index].getValue() != habitat[index + 1].getValue())
                    return false;
            }

            return true;
        }
    };

    public static Genome mostFit(GenomeBuilder builder, int populationSize, int numOffspring,
                                 float radioactivity, boolean social, boolean oneChild,
                                 Decider optimalFound, int numConverge, float punctuation) {
        // controls
        int fittestValue = 0; // highest value found thus far
        int nextGenValue = 0; // best value of next generation
        int consistent = 0;   // number of generations matching the highest value;

        // create the original organism
        Genome[] fittest = evolve(builder, populationSize, numOffspring, radioactivity,
                                  social, oneChild, optimalFound);
        Genome[] nextGen = new Genome[fittest.length];

        // update controls
        consistent += 1;

        while (consistent < numConverge) {
            // copy and mutate fittest genomes
            for (int index = 0; index < fittest.length; ++index) {
                // copy the organism
                nextGen[index] = builder.build(fittest[index]);

                // excepting the most fit organism, mutate
                if (index > 0)
                    mutate(nextGen[index], punctuation);
            }

            // create a new generation
            nextGen = evolve(builder, populationSize, numOffspring, radioactivity,
                             social, oneChild, optimalFound, nextGen);

            // update controls
            fittestValue = fittest[0].getValue();
            nextGenValue = nextGen[0].getValue();

            if (nextGenValue == fittestValue) {
                // update 'consistent' counter
                consistent += 1;
            }
            else if (nextGenValue > fittestValue) {
                // update fittest generation
                fittest = nextGen;

                // reset the 'consistent' counter
                consistent = 1;
            }
        }

        // return the fittest organism
        return fittest[0];
    }

    // modifies a genome according to the probability passed to the function
    public static void mutate(Genome offspring, float probability) {
        for (int index = 0; index < Genome.genes.length; ++index) {
            float result = (float)(Math.random());

            // if the probability threshold is high enough
            if (probability > result) {
                // flip a bit in the genome
                offspring.genome.flip(index);
            }
        }
    }

    /* This function generates several offspring genomes according to the
     * number of offspring specified.
     *
     * The splicing point is selected randomly.  For even-numbered offspring,
     * the resulting genome is the compliment of their preceding siblings.
     */
    private static Genome[] reproduce(GenomeBuilder builder, Genome mother, Genome father, int numOffspring) {
        // create new genomes
        Genome[] offspring = new Genome[numOffspring];

        for (int index = 0; index < numOffspring; ++index) {
            // determine index on which to split mother and father genomes
            int split = 0;

            if ((index & 1) == 0) { // if index is even
                // update split point
                split = (int)(Math.random() * Genome.genes.length);
                offspring[index] = builder.build(mother, father, split);
            }
            else {                // if index is odd
                offspring[index] = builder.build(mother, father, split,
                                                 EnumSet.of(Property.SWITCH_MOTHER_FATHER));
            }
        }

        return offspring;
    }

    /* This function creates an array of genomes sufficient to store the surviving
     * population for each generation, and all offspring created by the surviving
     * population.
     *
     * Indices 0 to (populationSize - 1) are to possess the surviving
     * population for each generation.  When new offspring are created, they
     * will be loaded into the indices between populationSize and the final
     * index of the array.
     *
     * After each reproductive cycle, the array is resorted and the optimal
     * offspring rise to the top.  Then, those genomes outside of the survival
     * range are replaced ("killed") when the next generation appears.
     */
    private static Genome[] makeHabitat(int populationSize, int numOffspring) {
        return new Genome[populationSize + ((populationSize / 2) * numOffspring)];
    }

    /* This function creates an array of genomes sufficient to store the population
     * size specified, and one child.
     *
     * Indices 0 to (populationSize - 1) possess the surviving members of each
     * generation, while index populationSize contains each generation's child.
     *
     * The least fit species is killed at the end of each generation.
     */
    private static Genome[] makeHabitat(int populationSize) {
        return new Genome[populationSize + 1];
    }

    // generate a genome after several generations of "evolution", use builder
    public static Genome[] evolve(GenomeBuilder builder, int populationSize,
                                  int numOffspring, float radioactivity, boolean social,
                                  boolean oneChild, Decider optimalFound) {
        // create a fresh habitat
        Genome[] habitat = oneChild ? makeHabitat(populationSize) : makeHabitat(populationSize, numOffspring);

        // fill the habitat with random genomes
        for (int index = 0; index < habitat.length; ++index)
            habitat[index] = builder.build(Genome.genes.length);

        // configure the decider
        optimalFound.populationSize = populationSize;

        // rank the fitness of the genomes
        Arrays.sort(habitat, Collections.reverseOrder());

        // while the most fit genome is inadequate, hunt for better genomes
        while (!optimalFound.decide(habitat)) {
            // create offspring
            int motherIndex = 0;                 // mothers are stored from 0 to (populationSize - 1), evens
            int fatherIndex = 0;                 // fathers are stored from 0 to (populationSize - 1), odds or random
            int offspringIndex = populationSize; // offspring are stored from populationSize to (habitat.length - 1)
            Genome[] offspring;

            // if only one parent
            if (populationSize == 1) {
                habitat[offspringIndex] = builder.build(habitat[motherIndex]);
            }
            else if (oneChild) {
                // if "social" reproduce with any partner
                if (social)
                    fatherIndex = (int)(Math.random() * populationSize);
                else
                    fatherIndex = motherIndex + 1;

                // create only one child
                habitat[offspringIndex] = builder.build(habitat[motherIndex],
                                                        habitat[fatherIndex]);
            }
            else {
                // distribute children
                while (motherIndex + 1 < populationSize) {
                    // if "social" reproduce with any partner
                    if (social)
                        fatherIndex = (int)(Math.random() * populationSize);
                    else
                        fatherIndex = motherIndex + 1;

                    // generate offspring for two parents
                    offspring = reproduce(builder,
                                          habitat[motherIndex],
                                          habitat[fatherIndex],
                                          numOffspring);

                    // place offspring in habitat
                    for (Genome child : offspring)
                        habitat[offspringIndex++] = child;

                    // increment to next two parents
                    motherIndex += 2;
                }
            }

            // randomly mutate some new genomes
            for (int index = populationSize; index < habitat.length; ++index)
                mutate(habitat[index], radioactivity);

            // determine the most fit
            Arrays.sort(habitat, Collections.reverseOrder());
        }

        // return the fittest genome
        return habitat;
    }

    // generate a genome after several generations of "evolution", take seed
    public static Genome[] evolve(GenomeBuilder builder, int populationSize,
                                  int numOffspring, float radioactivity, boolean social,
                                  boolean oneChild, Decider optimalFound, Genome[] seed) {
        // seed the habitat
        Genome[] habitat = seed;

        // configure the decider
        optimalFound.populationSize = populationSize;

        // rank the fitness of the genomes
        Arrays.sort(habitat, Collections.reverseOrder());

        // while the most fit genome is inadequate, hunt for better genomes
        while (!optimalFound.decide(habitat)) {
            // create offspring
            int motherIndex = 0;                 // mothers are stored from 0 to (populationSize - 1), evens
            int fatherIndex = 0;                 // fathers are stored from 0 to (populationSize - 1), odds or random
            int offspringIndex = populationSize; // offspring are stored from populationSize to (habitat.length - 1)
            Genome[] offspring;

            // if only one parent
            if (populationSize == 1) {
                habitat[offspringIndex] = builder.build(habitat[motherIndex]);
            }
            else if (oneChild) {
                // if "social" reproduce with any partner
                if (social)
                    fatherIndex = (int)(Math.random() * populationSize);
                else
                    fatherIndex = motherIndex + 1;

                // create only one child
                habitat[offspringIndex] = builder.build(habitat[motherIndex],
                                                        habitat[fatherIndex]);
            }
            else {
                // distribute children
                while (motherIndex + 1 < populationSize) {
                    // if "social" reproduce with any partner
                    if (social)
                        fatherIndex = (int)(Math.random() * populationSize);
                    else
                        fatherIndex = motherIndex + 1;

                    // generate offspring for two parents
                    offspring = reproduce(builder,
                                          habitat[motherIndex],
                                          habitat[fatherIndex],
                                          numOffspring);

                    // place offspring in habitat
                    for (Genome child : offspring)
                        habitat[offspringIndex++] = child;

                    // increment to next two parents
                    motherIndex += 2;
                }
            }

            // randomly mutate some new genomes
            for (int index = populationSize; index < habitat.length; ++index)
                mutate(habitat[index], radioactivity);

            // determine the most fit
            Arrays.sort(habitat, Collections.reverseOrder());
        }

        // return the fittest genome
        return habitat;
    }
}
