public class TrapezoidalFuzzySet extends FuzzySet {

    private final double a;
    private final double b;
    private final double c;
    private final double d;

    public TrapezoidalFuzzySet(String name, double a, double b, double c, double d) {
        super(name);
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double membershipDegree(double x) {
        if (x <= a || x >= d) {
            return 0.0;
        }
        if (x >= b && x <= c) {
            return 1.0;
        }
        if (x < b) {
            return (x - a) / (b - a);
        }
        return (d - x) / (d - c);
    }
}
