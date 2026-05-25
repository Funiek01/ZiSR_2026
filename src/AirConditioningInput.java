public class AirConditioningInput {

    private final double roomTemperatureCelsius;
    private final double targetTemperatureCelsius;
    private final double relativeHumidityPercent;

    public AirConditioningInput(double roomTemperatureCelsius,
                                double targetTemperatureCelsius,
                                double relativeHumidityPercent) {
        this.roomTemperatureCelsius = roomTemperatureCelsius;
        this.targetTemperatureCelsius = targetTemperatureCelsius;
        this.relativeHumidityPercent = relativeHumidityPercent;
    }

    public double roomTemperatureCelsius() {
        return roomTemperatureCelsius;
    }

    public double targetTemperatureCelsius() {
        return targetTemperatureCelsius;
    }

    public double relativeHumidityPercent() {
        return relativeHumidityPercent;
    }

    public double temperatureErrorCelsius() {
        return roomTemperatureCelsius - targetTemperatureCelsius;
    }

    @Override
    public String toString() {
        return String.format("room=%.1f C, target=%.1f C, humidity=%.1f%%",
                roomTemperatureCelsius,
                targetTemperatureCelsius,
                relativeHumidityPercent);
    }
}
