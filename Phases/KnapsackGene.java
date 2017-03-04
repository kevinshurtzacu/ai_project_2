public class KnapsackGene extends Gene {
    public int cost;
    public int value;
    
    public KnapsackGene(String n, int c, int v) {
        super(n);
        cost = c;
        value = v;
    }
}