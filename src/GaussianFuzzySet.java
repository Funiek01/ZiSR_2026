public class GaussianFuzzySet extends FuzzySet {

    private final double mean;
    private final double sigma;

    public GaussianFuzzySet(String name, double mean, double sigma) {
        super(name);
        this.mean = mean;
        this.sigma = sigma;
    }

    @Override
    public double membershipDegree(double x) {
        double d = x - mean;
        return Math.exp(-(d * d) / (2.0 * sigma * sigma));
    }
}
