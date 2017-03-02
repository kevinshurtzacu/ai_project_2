import java.util.BitSet;

// interface for implementing decision logic
abstract public class Decider {
    // keep track of candidate history
    public static int same = 1;
    public static Genome lastGeneration = null;
    public int generationCap = 0;
    
    // given a habitat, select whether an answer should be chosen
    abstract public boolean decide(Genome[] habitat);
    
    public void setGenerationCap(int genCap) {
        generationCap = genCap;
    }
}