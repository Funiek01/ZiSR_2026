/**
 * Trapezoidal fuzzy set defined by four parameters a, b, c, d where a <= b <= c <= d.
 *
 * Membership function:
 *   mu(x) = 0              if x <= a
 *   mu(x) = (x - a)/(b-a) if a < x <= b
 *   mu(x) = 1              if b < x <= c
 *   mu(x) = (d - x)/(d-c) if c < x < d
 *   mu(x) = 0              if x >= d
 *
 * Full membership (degree 1) is achieved over the plateau [b, c].
 */
public class TrapezoidalFuzzySet extends FuzzySet {

    private final double a;
    private final double b;
    private final double c;
    private final double d;

    /**
     * Constructs a trapezoidal fuzzy set.
     *
     * @param name the name of the fuzzy set
     * @param a    left foot of the trapezoid (lower boundary)
     * @param b    start of the top plateau
     * @param c    end of the top plateau
     * @param d    right foot of the trapezoid (upper boundary)
     * @throws IllegalArgumentException if a > b, b > c, or c > d
     */
    public TrapezoidalFuzzySet(String name, double a, double b, double c, double d) {
        super(name);
        if (a > b || b > c || c > d) {
            throw new IllegalArgumentException(
                    "Parameters must satisfy a <= b <= c <= d, got: a=" + a + ", b=" + b
                            + ", c=" + c + ", d=" + d);
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
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
     * Returns the start of the top plateau parameter b.
     *
     * @return b
     */
    public double getB() {
        return b;
    }

    /**
     * Returns the end of the top plateau parameter c.
     *
     * @return c
     */
    public double getC() {
        return c;
    }

    /**
     * Returns the right foot parameter d.
     *
     * @return d
     */
    public double getD() {
        return d;
    }

    @Override
    public double membershipDegree(double x) {
        if (x <= a || x >= d) {
            return 0.0;
        }
        if (x <= b) {
            return (b == a) ? 1.0 : (x - a) / (b - a);
        }
        if (x <= c) {
            return 1.0;
        }
        return (d == c) ? 1.0 : (d - x) / (d - c);
    }

    @Override
    public String toString() {
        return "TrapezoidalFuzzySet{name='" + getName() + "', a=" + a + ", b=" + b
                + ", c=" + c + ", d=" + d + "}";
    }
}
