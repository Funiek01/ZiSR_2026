public class AirConditioningOutput {

    private final double coolingPowerPercent;
    private final double fanSpeedPercent;
    private final double temperatureErrorCelsius;

    public AirConditioningOutput(double coolingPowerPercent,
                                 double fanSpeedPercent,
                                 double temperatureErrorCelsius) {
        this.coolingPowerPercent = coolingPowerPercent;
        this.fanSpeedPercent = fanSpeedPercent;
        this.temperatureErrorCelsius = temperatureErrorCelsius;
    }

    public double coolingPowerPercent() {
        return coolingPowerPercent;
    }

    public double fanSpeedPercent() {
        return fanSpeedPercent;
    }

    public double temperatureErrorCelsius() {
        return temperatureErrorCelsius;
    }

    @Override
    public String toString() {
        return String.format("error=%.1f C, cooling=%.2f%%, fan=%.2f%%",
                temperatureErrorCelsius,
                coolingPowerPercent,
                fanSpeedPercent);
    }
}
