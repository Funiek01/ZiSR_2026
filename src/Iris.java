public class Iris {

    private final String name;
    private final IrisProperty sepalLengthAvg;
    private final IrisProperty sepalWidthAvg;
    private final IrisProperty petalLengthAvg;
    private final IrisProperty petalWidthAvg;

    public Iris(String name,
                IrisProperty sepalLengthAvg,
                IrisProperty sepalWidthAvg,
                IrisProperty petalLengthAvg,
                IrisProperty petalWidthAvg) {
        this.name = name;
        this.sepalLengthAvg = sepalLengthAvg;
        this.sepalWidthAvg = sepalWidthAvg;
        this.petalLengthAvg = petalLengthAvg;
        this.petalWidthAvg = petalWidthAvg;
    }

    public String getName() {
        return name;
    }

    public IrisProperty getSepalLengthAvg() {
        return sepalLengthAvg;
    }

    public IrisProperty getSepalWidthAvg() {
        return sepalWidthAvg;
    }

    public IrisProperty getPetalLengthAvg() {
        return petalLengthAvg;
    }

    public IrisProperty getPetalWidthAvg() {
        return petalWidthAvg;
    }
}
