import java.util.BitSet;
import java.util.EnumSet;
import java.util.Scanner;
import java.io.InputStream;

// object to represent each gene configuration
abstract public class Genome implements Comparable {
    // data
    public static Gene[] genes;
    public BitSet genome;
    
    // input
    protected static Scanner scan;
    
    // create a random genome
    protected Genome() {
        genome = new BitSet();
        
        // randomly mix up genome
        for (int index = 0; index < genes.length; ++index) {
            // for every gene, 50% chance of flipping
            if ((int)(Math.random() * 2) == 0)
                genome.flip(index);
        }
    }
    
    // create a random genome of size 'length'
    protected Genome(int length) {
        genome = new BitSet(length);
        
        // randomly mix up genome
        for (int index = 0; index < genes.length; ++index) {
            // for every gene, 50% chance of flipping
            if ((int)(Math.random() * 2) == 0) {
                genome.flip(index);
            }
        }
    }
    
    // clone a genome
    protected Genome(Genome src) {
        genome = (BitSet)src.genome.clone();
    }
    
    // create a genome from a mother and a father
    protected Genome(Genome mother, Genome father) {
        // determine point at which to join mother and father
        int split = (int)(Math.random() * genes.length);
        
        // clone the mother
        genome = (BitSet)mother.genome.clone();
        
        // join mother and father
        genome.set(split, genes.length);
        genome.and(father.genome);
    }
    
    // create a genome from a mother and a father with flags
    protected Genome(Genome mother, Genome father, EnumSet<Property> properties) {
        // determine point at which to join mother and father
        int split = (int)(Math.random() * genes.length);
        
        // clone the mother
        genome = (BitSet)mother.genome.clone();
        
        // join mother and father
        if (properties.contains(Property.SWITCH_MOTHER_FATHER)) {
            genome.set(0, split);
            genome.and(father.genome);
        }
        else {
            genome.set(split, genes.length);
            genome.and(father.genome);
        }
    }
    
    // create a genome from a mother and a father with a designated split point
    protected Genome(Genome mother, Genome father, int split) {
        // clone the mother
        genome = (BitSet)mother.genome.clone();
        
        // join mother and father
        genome.set(split, genes.length);
        genome.and(father.genome);
    }
    
    // create a genome from a mother and a father with a designated split point and flags
    protected Genome(Genome mother, Genome father, int split, EnumSet<Property> properties) {
        // clone the mother
        genome = (BitSet)mother.genome.clone();
        
        // join mother and father
        if (properties.contains(Property.SWITCH_MOTHER_FATHER)) {
            genome.set(0, split);
            genome.and(father.genome);
        }
        else {
            genome.set(split, genes.length);
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
    
    // set the data's input source
    public static void setInput(InputStream in) {
        scan = new Scanner(in);
    }
    
    // return total value of genome
    abstract public int getValue();
}