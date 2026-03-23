public class TriangularFuzzySet extends FuzzySet {

    private final double a;
    private final double b;
    private final double c;

    public TriangularFuzzySet(String name, double a, double b, double c) {
        super(name);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double membershipDegree(double x) {
        if (x <= a || x >= c) {
            return 0.0;
        }
        if (x == b) {
            return 1.0;
        }
        if (x < b) {
            return (x - a) / (b - a);
        }
        return (c - x) / (c - b);
    }
}
