import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Scanner;

public class PhaseLib {
    private static Scanner scan = new Scanner(System.in);
    
    // returns true after N generations of the same best-fit candidate
    public static Decider generationNum = new Decider() {
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
        @Override
        public boolean decide(Genome[] habitat) {
            for (int index = 0; index < populationSize - 1; ++index) {
                if (habitat[index].getValue() != habitat[index + 1].getValue())
                    return false;
            }
            
            return true;
        }
    };
    
    // read capacity data
    public static int readCapacity() {
        // determine knapsack capacity
        int capacity = scan.nextInt();
        scan.nextLine();

        return capacity;
    }
    
    // read item data into List
    public static List<Node> readItems() {
        // read all items
        List<Node> items = new ArrayList<Node>();

        while (scan.hasNextLine()) {
            // read name, cost, value
            String line = scan.nextLine();
            String[] tokens = line.split(",");

            String name = tokens[0];
            int cost = Integer.parseInt(tokens[1]);
            int value = Integer.parseInt(tokens[2]);

            items.add(new Node(name, cost, value));
        }

        return items;
    }
    
    // modifies a genome according to the probability passed to the function
    public static void mutate(Genome offspring, float probability) {
        for (int index = 0; index < Genome.population.length; ++index) {
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
    public static Genome[] reproduce(Genome mother, Genome father, int numOffspring) {
        // create new genomes
        Genome[] offspring = new Genome[numOffspring];
        
        for (int index = 0; index < numOffspring; ++index) {
            // determine index on which to split mother and father genomes
            int split = 0;
            
            if ((index & 1) == 0) { // if index is even
                // update split point
                split = (int)(Math.random() * Genome.population.length);
                offspring[index] = new Genome(mother, father, split);
            }
            else {                // if index is odd
                offspring[index] = new Genome(mother, father, split, 
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
    public static Genome[] makeHabitat(int populationSize, int numOffspring) {
        return new Genome[populationSize + ((populationSize / 2) * numOffspring)];
    }
    
    // generate a genome after several generations of "evolution"
    public static Genome evolve(List<Node> nodes, int capacity, int populationSize,
                                int numOffspring, float radioactivity, boolean social,
                                Decider optimalFound) {
        // define population and capacity
        int nodeNum = nodes.size();
        Genome.population = nodes.toArray(new Node[nodeNum]);
        Genome.capacity = capacity;
        
        // create a fresh habitat
        Genome[] habitat = makeHabitat(populationSize, numOffspring);
        
        // fill the habitat with random genomes
        for (int index = 0; index < habitat.length; ++index)
            habitat[index] = new Genome(nodeNum);
        
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
            
            
            // if "social" reproduce with many partners
            while (motherIndex + 1 < populationSize) {
                if (social)
                    fatherIndex = (int)(Math.random() * populationSize);
                else
                    fatherIndex = motherIndex + 1;
                
                // generate offspring for two parents
                offspring = reproduce(habitat[motherIndex], 
                                      habitat[fatherIndex], 
                                      numOffspring);
                
                // place offspring in habitat
                for (Genome child : offspring)
                    habitat[offspringIndex++] = child;
                
                // increment to next two parents
                motherIndex += 2;
            }
            
            // randomly mutate some new genomes
            for (int index = populationSize; index < habitat.length; ++index)
                mutate(habitat[index], radioactivity);
            
            // determine the most fit
            Arrays.sort(habitat, Collections.reverseOrder());
        }
        
        // return the fittest genome
        return habitat[0];
    }
}