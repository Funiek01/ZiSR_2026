import fuzzlib.FuzzySet;

public class IrisProperty {

    private static final double MIN_WIDTH = 0.01;

    private final double avg;
    private final double dev;
    private final double gaussianWidth;
    private final FuzzySet fuzzySet;

    public IrisProperty(double avg, double dev) {
        this.avg = avg;
        this.dev = dev;
        this.gaussianWidth = Math.max(2.0 * dev, MIN_WIDTH);
        this.fuzzySet = new FuzzySet();
        this.fuzzySet.newGaussian(avg, gaussianWidth);
    }

    public double getAvg() {
        return avg;
    }

    public double getDev() {
        return dev;
    }

    public double getGaussianWidth() {
        return gaussianWidth;
    }

    public double getMembership(double comparedValue) {
        return fuzzySet.getMembership(comparedValue);
    }
}
