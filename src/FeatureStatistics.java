import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeatureStatistics {

    private final double mean;
    private final double median;
    private final double standardDeviation;
    private final double min;
    private final double max;

    private FeatureStatistics(double mean, double median, double standardDeviation, double min, double max) {
        this.mean = mean;
        this.median = median;
        this.standardDeviation = standardDeviation;
        this.min = min;
        this.max = max;
    }

    public static FeatureStatistics fromValues(List<Double> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values cannot be empty.");
        }

        List<Double> sorted = new ArrayList<Double>(values);
        Collections.sort(sorted);

        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }

        double mean = sum / values.size();
        double median;
        int middle = sorted.size() / 2;
        if (sorted.size() % 2 == 0) {
            median = (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
        } else {
            median = sorted.get(middle);
        }

        double varianceSum = 0.0;
        for (double value : values) {
            double difference = value - mean;
            varianceSum += difference * difference;
        }

        double standardDeviation = Math.sqrt(varianceSum / values.size());
        double min = sorted.get(0);
        double max = sorted.get(sorted.size() - 1);

        return new FeatureStatistics(mean, median, standardDeviation, min, max);
    }

    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
