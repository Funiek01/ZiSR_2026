import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fuzzlib.creators.OperationCreator;
import fuzzlib.norms.TNorm;

public class SimpleIrisGaussClassifier implements Classifier {

    public static final String IRIS_SETOSA = "Iris-setosa";
    public static final String IRIS_VERSICOLOR = "Iris-versicolor";
    public static final String IRIS_VIRGINICA = "Iris-virginica";

    private static final String[] FEATURE_NAMES = {
            "sepal length",
            "sepal width",
            "petal length",
            "petal width"
    };

    private final TNorm aggregationNorm = OperationCreator.newTNorm(TNorm.TN_PRODUCT);
    private final List<Iris> irisList;

    public SimpleIrisGaussClassifier(List<IrisSample> trainingSamples) {
        irisList = buildIrisList(trainingSamples);
    }

    public ClassificationOutput classify(ClassificationInput classificationInput) {
        ClassificationOutput bestOutput = null;

        for (Iris iris : irisList) {
            ClassificationOutput output = getMembershipForClass(iris, classificationInput);
            if (bestOutput == null || output.value() > bestOutput.value()) {
                bestOutput = output;
            }
        }

        if (bestOutput == null) {
            throw new IllegalStateException("No Iris classes are defined.");
        }

        return bestOutput;
    }

    public List<ClassificationOutput> scoreByClass(ClassificationInput classificationInput) {
        List<ClassificationOutput> scores = new ArrayList<ClassificationOutput>();

        for (Iris iris : irisList) {
            scores.add(getMembershipForClass(iris, classificationInput));
        }

        return scores;
    }

    public int countCorrect(List<IrisSample> samples) {
        int correct = 0;

        for (IrisSample sample : samples) {
            ClassificationInput input = toClassificationInput(sample);
            ClassificationOutput output = classify(input);
            if (sample.getLabel().equals(output.name())) {
                correct++;
            }
        }

        return correct;
    }

    public void printModel() {
        for (Iris iris : irisList) {
            System.out.println(iris.getName());
            printProperty("  " + FEATURE_NAMES[0], iris.getSepalLengthAvg());
            printProperty("  " + FEATURE_NAMES[1], iris.getSepalWidthAvg());
            printProperty("  " + FEATURE_NAMES[2], iris.getPetalLengthAvg());
            printProperty("  " + FEATURE_NAMES[3], iris.getPetalWidthAvg());
            System.out.println();
        }
    }

    private List<Iris> buildIrisList(List<IrisSample> trainingSamples) {
        Map<String, List<IrisSample>> samplesByClass = new LinkedHashMap<String, List<IrisSample>>();

        for (IrisSample sample : trainingSamples) {
            List<IrisSample> classSamples = samplesByClass.get(sample.getLabel());
            if (classSamples == null) {
                classSamples = new ArrayList<IrisSample>();
                samplesByClass.put(sample.getLabel(), classSamples);
            }
            classSamples.add(sample);
        }

        List<Iris> models = new ArrayList<Iris>();
        for (Map.Entry<String, List<IrisSample>> entry : samplesByClass.entrySet()) {
            models.add(new Iris(
                    entry.getKey(),
                    calculateProperty(entry.getValue(), 0),
                    calculateProperty(entry.getValue(), 1),
                    calculateProperty(entry.getValue(), 2),
                    calculateProperty(entry.getValue(), 3)
            ));
        }

        return models;
    }

    private ClassificationOutput getMembershipForClass(Iris iris, ClassificationInput classificationInput) {
        double value = 1.0;
        value = aggregationNorm.calc(value, calculateMembershipForProperty(iris.getPetalLengthAvg(), classificationInput.pl()));
        value = aggregationNorm.calc(value, calculateMembershipForProperty(iris.getPetalWidthAvg(), classificationInput.pw()));
        value = aggregationNorm.calc(value, calculateMembershipForProperty(iris.getSepalLengthAvg(), classificationInput.sl()));
        value = aggregationNorm.calc(value, calculateMembershipForProperty(iris.getSepalWidthAvg(), classificationInput.sw()));
        return new ClassificationOutput(iris.getName(), value);
    }

    private Double calculateMembershipForProperty(IrisProperty irisProperty, Double comparedValue) {
        return irisProperty.getMembership(comparedValue);
    }

    private IrisProperty calculateProperty(List<IrisSample> samples, int featureIndex) {
        List<Double> values = new ArrayList<Double>();

        for (IrisSample sample : samples) {
            values.add(sample.getFeature(featureIndex));
        }

        FeatureStatistics statistics = FeatureStatistics.fromValues(values);
        return new IrisProperty(statistics.getMean(), statistics.getStandardDeviation());
    }

    private ClassificationInput toClassificationInput(IrisSample sample) {
        return new ClassificationInput(
                sample.getFeature(0),
                sample.getFeature(1),
                sample.getFeature(2),
                sample.getFeature(3)
        );
    }

    private void printProperty(String name, IrisProperty property) {
        System.out.printf("%-16s avg=%.4f dev=%.4f gaussianWidth=%.4f%n",
                name,
                property.getAvg(),
                property.getDev(),
                property.getGaussianWidth());
    }
}
