// represents a single gene
abstract public class Gene {
    public String name;
    
    public Gene(String n) {
        name = n;
    }
    
    public String toString() {
        return name;
    }
}
