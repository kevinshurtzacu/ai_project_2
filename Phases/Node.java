// Item for comparisons in Phases 1-4
public class Node {
    public String name;
    public int cost;
    public int value;
    
    public Node(String n, int c, int v) {
        name = n;
        cost = c;
        value = v;
    }
    
    public String toString() {
        return name;
    }
}
