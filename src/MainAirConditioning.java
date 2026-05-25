import java.util.Locale;

public class MainAirConditioning {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        AirConditioningController controller = new AirConditioningController();

        if (args.length == 3) {
            AirConditioningInput input = new AirConditioningInput(
                    Double.parseDouble(args[0]),
                    Double.parseDouble(args[1]),
                    Double.parseDouble(args[2])
            );
            printSingleResult(controller, input);
            return;
        }

        if (args.length != 0) {
            printUsage();
            return;
        }

        printExamples(controller);
        printUsage();
    }

    private static void printExamples(AirConditioningController controller) {
        AirConditioningInput[] examples = new AirConditioningInput[] {
                new AirConditioningInput(20.5, 22.0, 42.0),
                new AirConditioningInput(22.5, 22.0, 76.0),
                new AirConditioningInput(25.0, 22.0, 55.0),
                new AirConditioningInput(27.0, 22.0, 80.0),
                new AirConditioningInput(30.0, 22.0, 68.0)
        };

        System.out.println("Fuzzy air conditioning controller");
        System.out.println("Inputs: room temperature, target temperature, relative humidity");
        System.out.println("Outputs: cooling compressor power and fan speed");
        System.out.println();
        System.out.printf("%9s %9s %9s %9s %10s %9s%n",
                "room C", "target C", "humidity", "error C", "cooling", "fan");

        for (int i = 0; i < examples.length; i++) {
            AirConditioningInput input = examples[i];
            AirConditioningOutput output = controller.control(input);
            System.out.printf("%9.1f %9.1f %8.1f%% %9.1f %9.2f%% %8.2f%%%n",
                    input.roomTemperatureCelsius(),
                    input.targetTemperatureCelsius(),
                    input.relativeHumidityPercent(),
                    output.temperatureErrorCelsius(),
                    output.coolingPowerPercent(),
                    output.fanSpeedPercent());
        }
        System.out.println();
    }

    private static void printSingleResult(AirConditioningController controller, AirConditioningInput input) {
        AirConditioningOutput output = controller.control(input);
        System.out.println("Input: " + input);
        System.out.println("Output: " + output);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java MainAirConditioning");
        System.out.println("  java MainAirConditioning <roomTempC> <targetTempC> <humidityPercent>");
        System.out.println("Example:");
        System.out.println("  java MainAirConditioning 27.0 22.0 75.0");
    }
}
