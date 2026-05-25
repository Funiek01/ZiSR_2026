import fuzzlib.DefuzMethod;
import fuzzlib.FuzzySet;
import fuzzlib.creators.OperationCreator;
import fuzzlib.norms.SNorm;
import fuzzlib.norms.TNorm;

public class AirConditioningController {

    private static final double MIN_TEMP_ERROR = -8.0;
    private static final double MAX_TEMP_ERROR = 10.0;
    private static final double MIN_HUMIDITY = 20.0;
    private static final double MAX_HUMIDITY = 90.0;
    private static final double MIN_OUTPUT = 0.0;
    private static final double MAX_OUTPUT = 100.0;

    private final TNorm andNorm = OperationCreator.newTNorm(TNorm.TN_MINIMUM);
    private final SNorm orNorm = OperationCreator.newSNorm(SNorm.SN_MAXIMUM);

    private final FuzzySet tooCold;
    private final FuzzySet comfortable;
    private final FuzzySet warm;
    private final FuzzySet hot;

    private final FuzzySet dry;
    private final FuzzySet normalHumidity;
    private final FuzzySet humid;

    private final FuzzySet coolingOff;
    private final FuzzySet coolingEco;
    private final FuzzySet coolingNormal;
    private final FuzzySet coolingStrong;
    private final FuzzySet coolingMaximum;

    private final FuzzySet fanSilent;
    private final FuzzySet fanLow;
    private final FuzzySet fanMedium;
    private final FuzzySet fanHigh;

    public AirConditioningController() {
        tooCold = leftShoulder(MIN_TEMP_ERROR, -3.5, -1.0);
        comfortable = triangle(-2.0, 0.0, 2.0);
        warm = triangle(0.5, 3.0, 5.5);
        hot = rightShoulder(4.0, 7.0, MAX_TEMP_ERROR);

        dry = leftShoulder(MIN_HUMIDITY, 35.0, 48.0);
        normalHumidity = trapezoid(35.0, 45.0, 60.0, 70.0);
        humid = rightShoulder(58.0, 74.0, MAX_HUMIDITY);

        coolingOff = leftShoulder(MIN_OUTPUT, 2.0, 10.0);
        coolingEco = triangle(10.0, 30.0, 50.0);
        coolingNormal = triangle(35.0, 55.0, 75.0);
        coolingStrong = triangle(60.0, 78.0, 94.0);
        coolingMaximum = rightShoulder(82.0, 94.0, MAX_OUTPUT);

        fanSilent = leftShoulder(MIN_OUTPUT, 18.0, 35.0);
        fanLow = triangle(25.0, 42.0, 60.0);
        fanMedium = triangle(50.0, 68.0, 84.0);
        fanHigh = rightShoulder(72.0, 88.0, MAX_OUTPUT);
    }

    public AirConditioningOutput control(AirConditioningInput input) {
        double temperatureError = clamp(input.temperatureErrorCelsius(), MIN_TEMP_ERROR, MAX_TEMP_ERROR);
        double humidity = clamp(input.relativeHumidityPercent(), MIN_HUMIDITY, MAX_HUMIDITY);

        double tooColdLevel = tooCold.getMembership(temperatureError);
        double comfortableLevel = comfortable.getMembership(temperatureError);
        double warmLevel = warm.getMembership(temperatureError);
        double hotLevel = hot.getMembership(temperatureError);

        double dryLevel = dry.getMembership(humidity);
        double normalHumidityLevel = normalHumidity.getMembership(humidity);
        double humidLevel = humid.getMembership(humidity);

        FuzzySet coolingResult = zeroOutputSet();
        FuzzySet fanResult = zeroOutputSet();

        applyRule(coolingResult, fanResult, tooColdLevel, coolingOff, fanSilent);
        applyRule(coolingResult, fanResult, and(comfortableLevel, dryLevel), coolingOff, fanLow);
        applyRule(coolingResult, fanResult, and(comfortableLevel, normalHumidityLevel), coolingOff, fanSilent);
        applyRule(coolingResult, fanResult, and(comfortableLevel, humidLevel), coolingEco, fanMedium);
        applyRule(coolingResult, fanResult, and(warmLevel, dryLevel), coolingEco, fanLow);
        applyRule(coolingResult, fanResult, and(warmLevel, normalHumidityLevel), coolingNormal, fanMedium);
        applyRule(coolingResult, fanResult, and(warmLevel, humidLevel), coolingStrong, fanHigh);
        applyRule(coolingResult, fanResult, and(hotLevel, or(dryLevel, normalHumidityLevel)), coolingStrong, fanHigh);
        applyRule(coolingResult, fanResult, and(hotLevel, humidLevel), coolingMaximum, fanHigh);

        coolingResult.PackFlatSections();
        fanResult.PackFlatSections();

        double cooling = clamp(coolingResult.DeFuzzyfyEx(DefuzMethod.DF_COG), MIN_OUTPUT, MAX_OUTPUT);
        double fan = clamp(fanResult.DeFuzzyfyEx(DefuzMethod.DF_COG), MIN_OUTPUT, MAX_OUTPUT);

        return new AirConditioningOutput(cooling, fan, input.temperatureErrorCelsius());
    }

    private void applyRule(FuzzySet coolingResult,
                           FuzzySet fanResult,
                           double activation,
                           FuzzySet coolingConsequent,
                           FuzzySet fanConsequent) {
        if (activation <= 0.0) {
            return;
        }

        aggregate(coolingResult, coolingConsequent, activation);
        aggregate(fanResult, fanConsequent, activation);
    }

    private void aggregate(FuzzySet accumulator, FuzzySet consequent, double activation) {
        FuzzySet clipped = new FuzzySet(consequent);
        clipped.processSetAndMembershipWithNorm(activation, andNorm);
        clipped.PackFlatSections();

        FuzzySet merged = new FuzzySet();
        FuzzySet.processSetsWithNorm(merged, accumulator, clipped, orNorm);
        accumulator.assign(merged);
    }

    private double and(double a, double b) {
        return andNorm.calc(a, b);
    }

    private double or(double a, double b) {
        return orNorm.calc(a, b);
    }

    private FuzzySet zeroOutputSet() {
        return line(MIN_OUTPUT, 0.0, MAX_OUTPUT, 0.0);
    }

    private static FuzzySet leftShoulder(double left, double fullUntil, double zeroAt) {
        FuzzySet set = new FuzzySet();
        set.addPoint(left, 1.0);
        set.addPoint(fullUntil, 1.0);
        set.addPoint(zeroAt, 0.0);
        return set;
    }

    private static FuzzySet rightShoulder(double zeroAt, double fullFrom, double right) {
        FuzzySet set = new FuzzySet();
        set.addPoint(zeroAt, 0.0);
        set.addPoint(fullFrom, 1.0);
        set.addPoint(right, 1.0);
        return set;
    }

    private static FuzzySet triangle(double left, double center, double right) {
        FuzzySet set = new FuzzySet();
        set.addPoint(left, 0.0);
        set.addPoint(center, 1.0);
        set.addPoint(right, 0.0);
        return set;
    }

    private static FuzzySet trapezoid(double left, double fullFrom, double fullUntil, double right) {
        FuzzySet set = new FuzzySet();
        set.addPoint(left, 0.0);
        set.addPoint(fullFrom, 1.0);
        set.addPoint(fullUntil, 1.0);
        set.addPoint(right, 0.0);
        return set;
    }

    private static FuzzySet line(double leftX, double leftY, double rightX, double rightY) {
        FuzzySet set = new FuzzySet();
        set.addPoint(leftX, leftY);
        set.addPoint(rightX, rightY);
        return set;
    }

    private static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
