/**
 * Abstract superclass representing a general fuzzy set.
 *
 * A fuzzy set is characterized by a membership function that maps
 * each element of the universe of discourse to a membership degree
 * in the interval [0, 1].
 */
public abstract class FuzzySet {

    private final String name;

    /**
     * Constructs a FuzzySet with the given name.
     *
     * @param name the name of the fuzzy set
     */
    public FuzzySet(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this fuzzy set.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Computes the membership degree of the given value x.
     *
     * @param x the input value
     * @return membership degree in [0, 1]
     */
    public abstract double membershipDegree(double x);

    /**
     * Returns a string describing the fuzzy set type and parameters.
     *
     * @return description string
     */
    @Override
    public abstract String toString();
}
