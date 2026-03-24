public class StatisticalFuzzySet extends FuzzySet {

    private final FeatureStatistics statistics;
    private final TrapezoidalFuzzySet trapezoid;
    private final double a;
    private final double b;
    private final double c;
    private final double d;

    public StatisticalFuzzySet(String name, FeatureStatistics statistics) {
        super(name);
        this.statistics = statistics;

        a = statistics.getMin();
        d = statistics.getMax();

        if (a == d) {
            b = a;
            c = d;
            trapezoid = null;
            return;
        }

        double center = (statistics.getMean() + statistics.getMedian()) / 2.0;
        double halfCoreWidth = statistics.getStandardDeviation() / 2.0;
        if (halfCoreWidth == 0.0) {
            halfCoreWidth = (d - a) / 10.0;
        }

        b = clamp(center - halfCoreWidth, a, d);
        c = clamp(center + halfCoreWidth, a, d);
        trapezoid = new TrapezoidalFuzzySet(name, a, b, c, d);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public double membershipDegree(double x) {
        if (trapezoid == null) {
            return x == a ? 1.0 : 0.0;
        }
        return trapezoid.membershipDegree(x);
    }

    public FeatureStatistics getStatistics() {
        return statistics;
    }

    public String getParametersText() {
        return String.format("[a=%.2f, b=%.2f, c=%.2f, d=%.2f]", a, b, c, d);
    }
}
