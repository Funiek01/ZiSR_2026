public class StatisticalFuzzySet {

    private static final double MIN_DISTANCE = 0.000001;

    private final FeatureStatistics statistics;
    private final fuzzlib.FuzzySet fuzzySet;
    private final double a;
    private final double b;
    private final double c;
    private final double d;

    public StatisticalFuzzySet(String name, FeatureStatistics statistics) {
        this.statistics = statistics;

        a = statistics.getMin();
        d = statistics.getMax();

        if (a == d) {
            b = a;
            c = d;
            fuzzySet = createSingletonLikeSet(name, a);
            return;
        }

        double center = (statistics.getMean() + statistics.getMedian()) / 2.0;
        double halfCoreWidth = statistics.getStandardDeviation() / 2.0;
        if (halfCoreWidth == 0.0) {
            halfCoreWidth = (d - a) / 10.0;
        }

        b = clamp(center - halfCoreWidth, a, d);
        c = clamp(center + halfCoreWidth, a, d);
        fuzzySet = createTrapezoid(name, a, b, c, d);
    }

    private fuzzlib.FuzzySet createSingletonLikeSet(String name, double value) {
        fuzzlib.FuzzySet set = new fuzzlib.FuzzySet(name, "statistical fuzzy set");
        set.addPoint(value - MIN_DISTANCE, 0.0);
        set.addPoint(value, 1.0);
        set.addPoint(value + MIN_DISTANCE, 0.0);
        return set;
    }

    private fuzzlib.FuzzySet createTrapezoid(String name, double left, double leftCore, double rightCore, double right) {
        fuzzlib.FuzzySet set = new fuzzlib.FuzzySet(name, "statistical fuzzy set");
        double range = Math.max(right - left, MIN_DISTANCE);
        double epsilon = Math.max(range / 1000.0, MIN_DISTANCE);

        double adjustedLeftCore = leftCore;
        double adjustedRightCore = rightCore;

        if (adjustedLeftCore <= left) {
            adjustedLeftCore = left + epsilon;
        }
        if (adjustedRightCore >= right) {
            adjustedRightCore = right - epsilon;
        }
        if (adjustedRightCore < adjustedLeftCore) {
            double middle = (left + right) / 2.0;
            adjustedLeftCore = clamp(middle, left + epsilon, right - epsilon);
            adjustedRightCore = adjustedLeftCore;
        }

        set.addPoint(left, 0.0);
        set.addPoint(adjustedLeftCore, 1.0);
        if (adjustedRightCore > adjustedLeftCore) {
            set.addPoint(adjustedRightCore, 1.0);
        }
        set.addPoint(right, 0.0);
        return set;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public double membershipDegree(double x) {
        return fuzzySet.getMembership(x);
    }

    public FeatureStatistics getStatistics() {
        return statistics;
    }

    public fuzzlib.FuzzySet getFuzzySet() {
        return fuzzySet;
    }

    public String getParametersText() {
        return String.format("[a=%.2f, b=%.2f, c=%.2f, d=%.2f]", a, b, c, d);
    }
}
