public abstract class FuzzySet {

    protected final String name;

    public FuzzySet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract double membershipDegree(double x);
}
