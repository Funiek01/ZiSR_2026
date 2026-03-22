/**
 * Triangular fuzzy set defined by three parameters a, b, c where a <= b <= c.
 *
 * Membership function:
 *   mu(x) = 0              if x <= a
 *   mu(x) = (x - a)/(b-a) if a < x <= b
 *   mu(x) = (c - x)/(c-b) if b < x < c
 *   mu(x) = 0              if x >= c
 *
 * The peak membership degree of 1 is reached at x = b.
 */
public class TriangularFuzzySet extends FuzzySet {

    private final double a;
    private final double b;
    private final double c;

    /**
     * Constructs a triangular fuzzy set.
     *
     * @param name the name of the fuzzy set
     * @param a    left foot of the triangle (lower boundary)
     * @param b    peak of the triangle (maximum membership)
     * @param c    right foot of the triangle (upper boundary)
     * @throws IllegalArgumentException if a > b or b > c
     */
    public TriangularFuzzySet(String name, double a, double b, double c) {
        super(name);
        if (a > b || b > c) {
            throw new IllegalArgumentException(
                    "Parameters must satisfy a <= b <= c, got: a=" + a + ", b=" + b + ", c=" + c);
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * Returns the left foot parameter a.
     *
     * @return a
     */
    public double getA() {
        return a;
    }

    /**
     * Returns the peak parameter b.
     *
     * @return b
     */
    public double getB() {
        return b;
    }

    /**
     * Returns the right foot parameter c.
     *
     * @return c
     */
    public double getC() {
        return c;
    }

    @Override
    public double membershipDegree(double x) {
        if (x <= a || x >= c) {
            return 0.0;
        }
        if (x <= b) {
            return (b == a) ? 1.0 : (x - a) / (b - a);
        }
        return (b == c) ? 1.0 : (c - x) / (c - b);
    }

    @Override
    public String toString() {
        return "TriangularFuzzySet{name='" + getName() + "', a=" + a + ", b=" + b + ", c=" + c + "}";
    }
}
