public class ClassificationInput {

    private final Double sl;
    private final Double sw;
    private final Double pl;
    private final Double pw;

    public ClassificationInput(Double sl, Double sw, Double pl, Double pw) {
        this.sl = sl;
        this.sw = sw;
        this.pl = pl;
        this.pw = pw;
    }

    public Double sl() {
        return sl;
    }

    public Double sw() {
        return sw;
    }

    public Double pl() {
        return pl;
    }

    public Double pw() {
        return pw;
    }

    public double[] toArray() {
        return new double[] { sl, sw, pl, pw };
    }

    @Override
    public String toString() {
        return "[" + sl + ", " + sw + ", " + pl + ", " + pw + "]";
    }
}
