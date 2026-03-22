/**
 * Gaussian fuzzy set defined by a mean (center) and standard deviation (sigma).
 *
 * Membership function:
 *   mu(x) = exp( -(x - mean)^2 / (2 * sigma^2) )
 *
 * The peak membership degree of 1 is reached at x = mean.
 * The set has infinite support but membership falls off rapidly beyond a few sigma.
 */
public class GaussianFuzzySet extends FuzzySet {

    private final double mean;
    private final double sigma;

    /**
     * Constructs a Gaussian fuzzy set.
     *
     * @param name  the name of the fuzzy set
     * @param mean  the center (peak) of the Gaussian curve
     * @param sigma the standard deviation; controls the spread of the curve
     * @throws IllegalArgumentException if sigma is not positive
     */
    public GaussianFuzzySet(String name, double mean, double sigma) {
        super(name);
        if (sigma <= 0) {
            throw new IllegalArgumentException(
                    "Sigma must be positive, got: " + sigma);
        }
        this.mean = mean;
        this.sigma = sigma;
    }

    /**
     * Returns the mean (center) of the Gaussian curve.
     *
     * @return mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * Returns the standard deviation of the Gaussian curve.
     *
     * @return sigma
     */
    public double getSigma() {
        return sigma;
    }

    @Override
    public double membershipDegree(double x) {
        double diff = x - mean;
        return Math.exp(-(diff * diff) / (2.0 * sigma * sigma));
    }

    @Override
    public String toString() {
        return "GaussianFuzzySet{name='" + getName() + "', mean=" + mean + ", sigma=" + sigma + "}";
    }
}
