// abstract class for implementing decision logic
abstract public class Decider {
    // keep track of candidate history
    public int same = 1;
    public Genome lastGeneration = null;
    
    // keep track of population information
    public int populationSize = 0;
    
    // configuration information
    public int generationCap = 100;
    public String name = "";
    
    // given a habitat, select whether an answer should be chosen
    abstract public boolean decide(Genome[] habitat);
}