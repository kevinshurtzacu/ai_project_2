import java.util.EnumSet;

// interface specifying the construction of genomes
abstract public class GenomeBuilder {
    abstract public Genome build();
    abstract public Genome build(int length);
    abstract public Genome build(Genome src);
    abstract public Genome build(Genome mother, Genome father);
    abstract public Genome build(Genome mother, Genome father, EnumSet<Property> properties);
    abstract public Genome build(Genome mother, Genome father, int split);
    abstract public Genome build(Genome mother, Genome father, int split, EnumSet<Property> properties);
}