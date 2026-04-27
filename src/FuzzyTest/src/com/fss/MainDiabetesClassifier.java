package com.fss;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import neuralnetwork.data.DataPackage;
import neuralnetwork.data.DataVector;

public class MainDiabetesClassifier {

	private static final String DEFAULT_DATA_FILE = "diabetes.data.csv";
	private static final int FEATURE_COUNT = 8;
	private static final int LABEL_COLUMN = 8;
	private static final long SPLIT_SEED = 20260428L;
	private static final double EPSILON = 0.000001;

	private static final String[] FEATURE_NAMES = {
			"pregnancies",
			"glucose",
			"blood pressure",
			"skin thickness",
			"insulin",
			"bmi",
			"pedigree",
			"age"
	};

	private static final boolean[] ZERO_IS_MISSING = {
			false,
			true,
			true,
			true,
			true,
			true,
			false,
			false
	};

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);

		String file = args.length > 0 ? args[0] : DEFAULT_DATA_FILE;
		DataPackage packageData = load(file);
		if (packageData == null) {
			return;
		}

		List<DataVector> rows = copyRows(packageData.getList());
		Split split = createStratifiedSplit(rows, 0.80, SPLIT_SEED);

		Model model = train(split.training);
		Evaluation holdoutEvaluation = evaluate(split.test, model);
		Evaluation crossValidation = crossValidate(rows, 5);

		System.out.println("Dataset: " + file);
		System.out.println("Rows: " + rows.size());
		System.out.println("Features: " + FEATURE_COUNT);
		System.out.println("Label column: " + LABEL_COLUMN + " (0 = no diabetes, 1 = diabetes)");
		System.out.println();

		System.out.println("Holdout test: 80% train / 20% test");
		System.out.println("  train rows: " + split.training.size());
		System.out.println("  test rows: " + split.test.size());
		printEvaluation(holdoutEvaluation, "  ");
		System.out.println();

		System.out.println("5-fold cross-validation:");
		printEvaluation(crossValidation, "  ");
		System.out.println();

		printModel(model);
	}

	private static DataPackage load(String file) {
		DataPackage data = new DataPackage();
		data.setFieldSeparator(",");
		data.setDecimalSeparator('.');

		String resolvedFile = resolveDataFile(file);
		if (resolvedFile == null) {
			System.out.println("Cannot load data file: " + file);
			System.out.println("Current directory: " + new File(".").getAbsolutePath());
			System.out.println("Tried:");
			System.out.println("  " + file);
			System.out.println("  src/FuzzyTest/" + file);
			System.out.println("  ../" + file);
			System.out.println("  ../src/FuzzyTest/" + file);
			return null;
		}

		if (!data.loadTextFile(resolvedFile)) {
			System.out.println("Cannot load data file: " + resolvedFile);
			return null;
		}

		if (data.size() == 0) {
			System.out.println("The data file is empty: " + file);
			return null;
		}

		if (data.getMinRowSize() != FEATURE_COUNT + 1 || data.getMaxRowSize() != FEATURE_COUNT + 1) {
			System.out.println("Expected 9 numeric columns in every row, but found row sizes from "
					+ data.getMinRowSize() + " to " + data.getMaxRowSize());
			return null;
		}

		return data;
	}

	private static String resolveDataFile(String file) {
		String[] candidates = {
				file,
				"src/FuzzyTest/" + file,
				"../" + file,
				"../src/FuzzyTest/" + file
		};

		for (String candidate : candidates) {
			File candidateFile = new File(candidate);
			if (candidateFile.isFile()) {
				return candidateFile.getPath();
			}
		}

		return null;
	}

	private static List<DataVector> copyRows(List<DataVector> source) {
		List<DataVector> copy = new ArrayList<DataVector>();
		for (DataVector row : source) {
			copy.add(new DataVector(row));
		}
		return copy;
	}

	private static Split createStratifiedSplit(List<DataVector> rows, double trainRatio, long seed) {
		Map<Integer, List<DataVector>> byClass = groupByClass(rows);
		List<DataVector> training = new ArrayList<DataVector>();
		List<DataVector> test = new ArrayList<DataVector>();

		for (Map.Entry<Integer, List<DataVector>> entry : byClass.entrySet()) {
			List<DataVector> classRows = copyRows(entry.getValue());
			Collections.shuffle(classRows, new Random(seed + entry.getKey()));

			int trainSize = (int) Math.round(classRows.size() * trainRatio);
			if (trainSize < 1) {
				trainSize = 1;
			}
			if (trainSize >= classRows.size()) {
				trainSize = classRows.size() - 1;
			}

			for (int i = 0; i < classRows.size(); i++) {
				if (i < trainSize) {
					training.add(classRows.get(i));
				} else {
					test.add(classRows.get(i));
				}
			}
		}

		sortRowsByLabel(training);
		sortRowsByLabel(test);
		return new Split(training, test);
	}

	private static Evaluation crossValidate(List<DataVector> rows, int folds) {
		Map<Integer, List<DataVector>> byClass = groupByClass(rows);
		Evaluation total = new Evaluation();

		for (int fold = 0; fold < folds; fold++) {
			List<DataVector> training = new ArrayList<DataVector>();
			List<DataVector> test = new ArrayList<DataVector>();

			for (Map.Entry<Integer, List<DataVector>> entry : byClass.entrySet()) {
				List<DataVector> classRows = copyRows(entry.getValue());
				Collections.shuffle(classRows, new Random(SPLIT_SEED + entry.getKey()));

				for (int i = 0; i < classRows.size(); i++) {
					if (i % folds == fold) {
						test.add(classRows.get(i));
					} else {
						training.add(classRows.get(i));
					}
				}
			}

			total.add(evaluate(test, train(training)));
		}

		return total;
	}

	private static Map<Integer, List<DataVector>> groupByClass(List<DataVector> rows) {
		Map<Integer, List<DataVector>> byClass = new LinkedHashMap<Integer, List<DataVector>>();

		for (DataVector row : rows) {
			Integer label = Integer.valueOf(getLabel(row));
			List<DataVector> classRows = byClass.get(label);
			if (classRows == null) {
				classRows = new ArrayList<DataVector>();
				byClass.put(label, classRows);
			}
			classRows.add(row);
		}

		return byClass;
	}

	private static void sortRowsByLabel(List<DataVector> rows) {
		Collections.sort(rows, new Comparator<DataVector>() {
			public int compare(DataVector first, DataVector second) {
				return getLabel(first) - getLabel(second);
			}
		});
	}

	private static Model train(List<DataVector> trainingRows) {
		Model model = new Model();
		model.imputeValues = calculateImputeValues(trainingRows);
		model.globalStdDevs = calculateGlobalStdDevs(trainingRows, model.imputeValues);
		model.classModels = new LinkedHashMap<Integer, ClassModel>();

		Map<Integer, List<DataVector>> byClass = groupByClass(trainingRows);
		for (Map.Entry<Integer, List<DataVector>> entry : byClass.entrySet()) {
			ClassModel classModel = new ClassModel();
			classModel.label = entry.getKey().intValue();
			classModel.prior = (double) entry.getValue().size() / trainingRows.size();
			classModel.features = new FeatureModel[FEATURE_COUNT];

			for (int feature = 0; feature < FEATURE_COUNT; feature++) {
				classModel.features[feature] = calculateFeatureModel(entry.getValue(), feature, model);
			}

			model.classModels.put(entry.getKey(), classModel);
		}

		return model;
	}

	private static double[] calculateImputeValues(List<DataVector> rows) {
		double[] sums = new double[FEATURE_COUNT];
		int[] counts = new int[FEATURE_COUNT];

		for (DataVector row : rows) {
			for (int feature = 0; feature < FEATURE_COUNT; feature++) {
				double value = row.get(feature);
				if (!isMissing(feature, value)) {
					sums[feature] += value;
					counts[feature]++;
				}
			}
		}

		double[] means = new double[FEATURE_COUNT];
		for (int feature = 0; feature < FEATURE_COUNT; feature++) {
			means[feature] = counts[feature] == 0 ? 0.0 : sums[feature] / counts[feature];
		}

		return means;
	}

	private static double[] calculateGlobalStdDevs(List<DataVector> rows, double[] imputeValues) {
		double[] sums = new double[FEATURE_COUNT];
		double[] squaredSums = new double[FEATURE_COUNT];

		for (DataVector row : rows) {
			for (int feature = 0; feature < FEATURE_COUNT; feature++) {
				double value = replaceMissing(feature, row.get(feature), imputeValues);
				sums[feature] += value;
				squaredSums[feature] += value * value;
			}
		}

		double[] stdDevs = new double[FEATURE_COUNT];
		for (int feature = 0; feature < FEATURE_COUNT; feature++) {
			double mean = sums[feature] / rows.size();
			double variance = squaredSums[feature] / rows.size() - mean * mean;
			stdDevs[feature] = Math.sqrt(Math.max(variance, EPSILON));
		}

		return stdDevs;
	}

	private static FeatureModel calculateFeatureModel(List<DataVector> rows, int feature, Model model) {
		double sum = 0.0;
		int count = 0;

		for (DataVector row : rows) {
			double value = row.get(feature);
			if (!isMissing(feature, value)) {
				sum += value;
				count++;
			}
		}

		double mean = count == 0 ? model.imputeValues[feature] : sum / count;
		double varianceSum = 0.0;

		for (DataVector row : rows) {
			double value = row.get(feature);
			if (!isMissing(feature, value)) {
				double difference = value - mean;
				varianceSum += difference * difference;
			}
		}

		double stdDev = count <= 1 ? model.globalStdDevs[feature] : Math.sqrt(varianceSum / count);
		if (stdDev < EPSILON) {
			stdDev = model.globalStdDevs[feature];
		}
		if (stdDev < EPSILON) {
			stdDev = 1.0;
		}

		FeatureModel featureModel = new FeatureModel();
		featureModel.mean = mean;
		featureModel.stdDev = stdDev;
		featureModel.trainingValues = count;
		return featureModel;
	}

	private static Evaluation evaluate(List<DataVector> testRows, Model model) {
		Evaluation evaluation = new Evaluation();

		for (DataVector row : testRows) {
			int expected = getLabel(row);
			int predicted = classify(row, model);
			evaluation.addPrediction(expected, predicted);
		}

		return evaluation;
	}

	private static int classify(DataVector row, Model model) {
		int bestClass = -1;
		double bestScore = Double.NEGATIVE_INFINITY;

		for (ClassModel classModel : model.classModels.values()) {
			double score = Math.log(classModel.prior);

			for (int feature = 0; feature < FEATURE_COUNT; feature++) {
				double value = replaceMissing(feature, row.get(feature), model.imputeValues);
				double membership = gaussianMembership(value, classModel.features[feature].mean,
						classModel.features[feature].stdDev);
				score += Math.log(Math.max(membership, EPSILON)) - Math.log(classModel.features[feature].stdDev);
			}

			if (score > bestScore) {
				bestScore = score;
				bestClass = classModel.label;
			}
		}

		return bestClass;
	}

	private static double gaussianMembership(double value, double center, double width) {
		double difference = value - center;
		return Math.exp(-(difference * difference) / (2.0 * width * width));
	}

	private static double replaceMissing(int feature, double value, double[] imputeValues) {
		return isMissing(feature, value) ? imputeValues[feature] : value;
	}

	private static boolean isMissing(int feature, double value) {
		return ZERO_IS_MISSING[feature] && value == 0.0;
	}

	private static int getLabel(DataVector row) {
		return (int) Math.round(row.get(LABEL_COLUMN));
	}

	private static void printEvaluation(Evaluation evaluation, String indent) {
		System.out.printf(indent + "accuracy: %.2f%% (%d/%d)%n",
				evaluation.getAccuracy() * 100.0,
				evaluation.correct,
				evaluation.total);
		System.out.printf(indent + "precision for class 1: %.2f%%%n", evaluation.getPrecision() * 100.0);
		System.out.printf(indent + "recall for class 1: %.2f%%%n", evaluation.getRecall() * 100.0);
		System.out.printf(indent + "F1 for class 1: %.2f%%%n", evaluation.getF1() * 100.0);
		System.out.println(indent + "confusion matrix:");
		System.out.println(indent + "  actual 0 -> predicted 0: " + evaluation.trueNegative);
		System.out.println(indent + "  actual 0 -> predicted 1: " + evaluation.falsePositive);
		System.out.println(indent + "  actual 1 -> predicted 0: " + evaluation.falseNegative);
		System.out.println(indent + "  actual 1 -> predicted 1: " + evaluation.truePositive);
	}

	private static void printModel(Model model) {
		System.out.println("Learned fuzzy Gaussian model from holdout training set:");
		for (ClassModel classModel : model.classModels.values()) {
			System.out.println("  class " + classModel.label + " prior=" + format(classModel.prior));
			for (int feature = 0; feature < FEATURE_COUNT; feature++) {
				FeatureModel featureModel = classModel.features[feature];
				System.out.println("    " + FEATURE_NAMES[feature]
						+ ": center=" + format(featureModel.mean)
						+ ", width=" + format(featureModel.stdDev)
						+ ", values=" + featureModel.trainingValues);
			}
		}
	}

	private static String format(double value) {
		return String.format(Locale.US, "%.4f", value);
	}

	private static final class Split {
		private final List<DataVector> training;
		private final List<DataVector> test;

		private Split(List<DataVector> training, List<DataVector> test) {
			this.training = training;
			this.test = test;
		}
	}

	private static final class Model {
		private Map<Integer, ClassModel> classModels;
		private double[] imputeValues;
		private double[] globalStdDevs;
	}

	private static final class ClassModel {
		private int label;
		private double prior;
		private FeatureModel[] features;
	}

	private static final class FeatureModel {
		private double mean;
		private double stdDev;
		private int trainingValues;
	}

	private static final class Evaluation {
		private int truePositive;
		private int trueNegative;
		private int falsePositive;
		private int falseNegative;
		private int correct;
		private int total;

		private void addPrediction(int expected, int predicted) {
			if (expected == predicted) {
				correct++;
			}
			total++;

			if (expected == 1 && predicted == 1) {
				truePositive++;
			} else if (expected == 0 && predicted == 0) {
				trueNegative++;
			} else if (expected == 0 && predicted == 1) {
				falsePositive++;
			} else if (expected == 1 && predicted == 0) {
				falseNegative++;
			}
		}

		private void add(Evaluation other) {
			truePositive += other.truePositive;
			trueNegative += other.trueNegative;
			falsePositive += other.falsePositive;
			falseNegative += other.falseNegative;
			correct += other.correct;
			total += other.total;
		}

		private double getAccuracy() {
			return total == 0 ? 0.0 : (double) correct / total;
		}

		private double getPrecision() {
			int predictedPositive = truePositive + falsePositive;
			return predictedPositive == 0 ? 0.0 : (double) truePositive / predictedPositive;
		}

		private double getRecall() {
			int actualPositive = truePositive + falseNegative;
			return actualPositive == 0 ? 0.0 : (double) truePositive / actualPositive;
		}

		private double getF1() {
			double precision = getPrecision();
			double recall = getRecall();
			return precision + recall == 0.0 ? 0.0 : 2.0 * precision * recall / (precision + recall);
		}
	}
}
