import java.util.BitSet;
import java.util.EnumSet;

// object to represent each gene configuration
public class Genome implements Comparable {
    public static Node[] population;
    public static int capacity;
    public BitSet genome;
    
    // create a random genome
    public Genome() {
        genome = new BitSet();
        
        // randomly mix up genome
        for (int index = 0; index < population.length; ++index) {
            // for every gene, 50% chance of flipping
            if ((int)(Math.random() * 2) == 0)
                genome.flip(index);
        }
    }
    
    // create a random genome of size 'length'
    public Genome(int length) {
        genome = new BitSet(length);
        
        // randomly mix up genome
        for (int index = 0; index < population.length; ++index) {
            // for every gene, 50% chance of flipping
            if ((int)(Math.random() * 2) == 0) {
                genome.flip(index);
            }
        }
    }
    
    // clone a genome
    public Genome(Genome src) {
        genome = (BitSet)src.genome.clone();
    }
    
    // create a genome from a mother and a father
    public Genome(Genome mother, Genome father) {
        // determine point at which to join mother and father
        int split = (int)(Math.random() * population.length);
        
        // clone the mother
        genome = (BitSet)mother.genome.clone();
        
        // join mother and father
        genome.set(split, population.length);
        genome.and(father.genome);
    }
    
    // create a genome from a mother and a father with flags
    public Genome(Genome mother, Genome father, EnumSet<Property> properties) {
        // determine point at which to join mother and father
        int split = (int)(Math.random() * population.length);
        
        // clone the mother
        genome = (BitSet)mother.genome.clone();
        
        // join mother and father
        if (properties.contains(Property.SWITCH_MOTHER_FATHER)) {
            genome.set(0, split);
            genome.and(father.genome);
        }
        else {
            genome.set(split, population.length);
            genome.and(father.genome);
        }
    }
    
    // create a genome from a mother and a father with a designated split point
    public Genome(Genome mother, Genome father, int split) {
        // clone the mother
        genome = (BitSet)mother.genome.clone();
        
        // join mother and father
        genome.set(split, population.length);
        genome.and(father.genome);
    }
    
    // create a genome from a mother and a father with a designated split point and flags
    public Genome(Genome mother, Genome father, int split, EnumSet<Property> properties) {
        // clone the mother
        genome = (BitSet)mother.genome.clone();
        
        // join mother and father
        if (properties.contains(Property.SWITCH_MOTHER_FATHER)) {
            genome.set(0, split);
            genome.and(father.genome);
        }
        else {
            genome.set(split, population.length);
            genome.and(father.genome);
        }
    }
    
    // compare the value of two genomes
    public int compareTo(Object other) {
        // cast other object to Genome
        Genome otherGenome = (Genome)other;

        // make comparison
        int value = getValue();
        int otherValue = otherGenome.getValue();
        
        if (value < otherValue)
            return -1;
        else if (value > otherValue)
            return 1;
        else
            return 0;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        
        if (other == null)
            return false;
        
        if (getClass() != other.getClass())
            return false;
        
        return genome.equals(((Genome)other).genome);
    }
    
    @Override
    public int hashCode() {
        return genome.hashCode();
    }
    
    // return total value of genome
    public int getValue() {
        int value = 0;
        int cost = 0;
        
        // total the value of the genome
        for (int bitIndex = genome.nextSetBit(0); 
                bitIndex != -1; 
                bitIndex = genome.nextSetBit(bitIndex + 1)) {
            value += population[bitIndex].value;
            cost  += population[bitIndex].cost;
        }
        
        // if the cost exceeds capacity the genome loses its value
        if (cost > capacity)
            value = (capacity - cost);
        
        return value;
    }
}