import java.util.Arrays;

public class IrisSample {

    private final double[] features;
    private final String label;

    public IrisSample(double[] features, String label) {
        this.features = Arrays.copyOf(features, features.length);
        this.label = label;
    }

    public double[] getFeatures() {
        return Arrays.copyOf(features, features.length);
    }

    public double getFeature(int index) {
        return features[index];
    }

    public String getLabel() {
        return label;
    }
}
