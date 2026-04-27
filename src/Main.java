import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final String DATA_FILE = "data/iris.data";

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        try {
            List<IrisSample> samples = IrisDataLoader.load(DATA_FILE);
            SimpleIrisGaussClassifier classifier = new SimpleIrisGaussClassifier(samples);

            System.out.println("Gaussian fuzzy Iris classifier built from: " + DATA_FILE);
            System.out.println("Library: fuzzlib.FuzzySet.newGaussian + TN_PRODUCT aggregation");
            System.out.println();

            System.out.println("Learned model:");
            classifier.printModel();

            int correct = classifier.countCorrect(samples);
            double accuracy = 100.0 * correct / samples.size();
            System.out.printf("Accuracy on the whole Iris dataset: %.2f%% (%d/%d)%n", accuracy, correct, samples.size());
            System.out.println();

            if (args.length == 4) {
                ClassificationInput input = parseInput(args);
                printPrediction(classifier, input);
                return;
            }

            if (args.length != 0) {
                printUsage();
                return;
            }

            showClassification(classifier);
            printUsage();
        } catch (IOException e) {
            System.out.println("Cannot load dataset: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            printUsage();
        }
    }

    private static void showClassification(SimpleIrisGaussClassifier classifier) {
        List<ClassificationInput> classificationInputs = new ArrayList<ClassificationInput>();
        classificationInputs.add(new ClassificationInput(5.4, 3.4, 1.7, 0.2));
        classificationInputs.add(new ClassificationInput(6.7, 3.0, 5.0, 1.7));
        classificationInputs.add(new ClassificationInput(6.8, 3.0, 5.5, 2.1));
        classificationInputs.add(new ClassificationInput(6.6, 2.9, 4.6, 1.3));
        classificationInputs.add(new ClassificationInput(4.6, 3.1, 1.5, 0.2));

        System.out.println("Example classifications:");
        for (ClassificationInput classificationInput : classificationInputs) {
            printPrediction(classifier, classificationInput);
        }
    }

    private static ClassificationInput parseInput(String[] args) {
        return new ClassificationInput(
                Double.parseDouble(args[0]),
                Double.parseDouble(args[1]),
                Double.parseDouble(args[2]),
                Double.parseDouble(args[3])
        );
    }

    private static void printPrediction(SimpleIrisGaussClassifier classifier, ClassificationInput input) {
        ClassificationOutput output = classifier.classify(input);

        System.out.println("  input=" + input);
        System.out.println("  scores:");
        for (ClassificationOutput score : classifier.scoreByClass(input)) {
            System.out.printf("    %-15s -> %.8f%n", score.name(), score.value());
        }
        System.out.println("  predicted class: " + output.name());
        System.out.println();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java Main");
        System.out.println("  java Main <sepalLength> <sepalWidth> <petalLength> <petalWidth>");
        System.out.println("Example:");
        System.out.println("  java Main 5.1 3.5 1.4 0.2");
    }
}
