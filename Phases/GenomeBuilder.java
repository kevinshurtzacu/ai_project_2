import java.util.EnumSet;

// interface specifying the construction of genomes
public interface GenomeBuilder {
    Genome build();
    Genome build(int length);
    Genome build(Genome src);
    Genome build(Genome mother, Genome father);
    Genome build(Genome mother, Genome father, EnumSet<Property> properties);
    Genome build(Genome mother, Genome father, int split);
    Genome build(Genome mother, Genome father, int split, EnumSet<Property> properties);
}
