public class ClassificationOutput {

    private final String name;
    private final Double value;

    public ClassificationOutput(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public Double value() {
        return value;
    }

    @Override
    public String toString() {
        return name + " -> " + value;
    }
}
