import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FuzzyClassifier {

    public static final String[] FEATURE_NAMES = {
            "sepal length",
            "sepal width",
            "petal length",
            "petal width"
    };

    private final Map<String, StatisticalFuzzySet[]> setsByClass = new LinkedHashMap<String, StatisticalFuzzySet[]>();

    public FuzzyClassifier(List<IrisSample> trainingSamples) {
        buildModel(trainingSamples);
    }

    private void buildModel(List<IrisSample> trainingSamples) {
        Map<String, List<IrisSample>> samplesByClass = new LinkedHashMap<String, List<IrisSample>>();

        for (IrisSample sample : trainingSamples) {
            if (!samplesByClass.containsKey(sample.getLabel())) {
                samplesByClass.put(sample.getLabel(), new ArrayList<IrisSample>());
            }
            samplesByClass.get(sample.getLabel()).add(sample);
        }

        for (Map.Entry<String, List<IrisSample>> entry : samplesByClass.entrySet()) {
            StatisticalFuzzySet[] classSets = new StatisticalFuzzySet[FEATURE_NAMES.length];

            for (int featureIndex = 0; featureIndex < FEATURE_NAMES.length; featureIndex++) {
                List<Double> values = new ArrayList<Double>();
                for (IrisSample sample : entry.getValue()) {
                    values.add(sample.getFeature(featureIndex));
                }

                FeatureStatistics statistics = FeatureStatistics.fromValues(values);
                classSets[featureIndex] = new StatisticalFuzzySet(
                        entry.getKey() + " - " + FEATURE_NAMES[featureIndex],
                        statistics
                );
            }

            setsByClass.put(entry.getKey(), classSets);
        }
    }

    public Map<String, Double> scoreByClass(double[] features) {
        Map<String, Double> scores = new LinkedHashMap<String, Double>();

        for (Map.Entry<String, StatisticalFuzzySet[]> entry : setsByClass.entrySet()) {
            double score = 0.0;
            StatisticalFuzzySet[] classSets = entry.getValue();

            for (int i = 0; i < classSets.length; i++) {
                score += classSets[i].membershipDegree(features[i]);
            }

            scores.put(entry.getKey(), score);
        }

        return scores;
    }

    public String classify(double[] features) {
        Map<String, Double> scores = scoreByClass(features);
        String bestClass = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestClass = entry.getKey();
            }
        }

        return bestClass;
    }

    public int countCorrect(List<IrisSample> samples) {
        int correct = 0;
        for (IrisSample sample : samples) {
            String predicted = classify(sample.getFeatures());
            if (sample.getLabel().equals(predicted)) {
                correct++;
            }
        }
        return correct;
    }

    public void printModel() {
        for (Map.Entry<String, StatisticalFuzzySet[]> entry : setsByClass.entrySet()) {
            System.out.println(entry.getKey());

            StatisticalFuzzySet[] classSets = entry.getValue();
            for (int i = 0; i < classSets.length; i++) {
                FeatureStatistics statistics = classSets[i].getStatistics();
                System.out.printf(
                        "  %-12s mean=%.2f median=%.2f std=%.2f min=%.2f max=%.2f %s%n",
                        FEATURE_NAMES[i],
                        statistics.getMean(),
                        statistics.getMedian(),
                        statistics.getStandardDeviation(),
                        statistics.getMin(),
                        statistics.getMax(),
                        classSets[i].getParametersText()
                );
            }
            System.out.println();
        }
    }
}
