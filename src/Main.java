import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Main {

    private static final String DATA_FILE = "data/iris.data";

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        try {
            List<IrisSample> samples = IrisDataLoader.load(DATA_FILE);
            FuzzyClassifier classifier = new FuzzyClassifier(samples);

            System.out.println("Fuzzy classifier built from: " + DATA_FILE);
            System.out.println();
            classifier.printModel();

            if (args.length == 4) {
                double[] features = parseFeatures(args);
                printPrediction(classifier, features);
                return;
            }

            if (args.length != 0) {
                printUsage();
                return;
            }

            int correct = classifier.countCorrect(samples);
            double accuracy = 100.0 * correct / samples.size();
            System.out.printf("Accuracy on the whole Iris dataset: %.2f%% (%d/%d)%n", accuracy, correct, samples.size());
            System.out.println();

            System.out.println("Example classifications:");
            for (int i = 0; i < 3 && i < samples.size(); i++) {
                IrisSample sample = samples.get(i);
                System.out.printf("  real=%s features=%s%n", sample.getLabel(), Arrays.toString(sample.getFeatures()));
                printPrediction(classifier, sample.getFeatures());
            }

            System.out.println("Run with 4 numbers to classify your own vector.");
            System.out.println("Example: java Main 5.1 3.5 1.4 0.2");
        } catch (IOException e) {
            System.out.println("Cannot load dataset: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            printUsage();
        }
    }

    private static double[] parseFeatures(String[] args) {
        double[] features = new double[4];
        for (int i = 0; i < 4; i++) {
            features[i] = Double.parseDouble(args[i]);
        }
        return features;
    }

    private static void printPrediction(FuzzyClassifier classifier, double[] features) {
        Map<String, Double> scores = classifier.scoreByClass(features);
        String predictedClass = classifier.classify(features);

        System.out.println("  scores:");
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            System.out.printf("    %-15s -> %.4f%n", entry.getKey(), entry.getValue());
        }
        System.out.println("  predicted class: " + predictedClass);
        System.out.println();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java Main");
        System.out.println("  java Main <sepalLength> <sepalWidth> <petalLength> <petalWidth>");
    }
}
