public class Main {

    public static void main(String[] args) {
        FuzzySet triangular = new TriangularFuzzySet("triangular", 10, 20, 30);
        FuzzySet trapezoidal = new TrapezoidalFuzzySet("trapezoidal", 15, 18, 24, 27);
        FuzzySet gaussian = new GaussianFuzzySet("gaussian", 20, 3);

        double[] xs = {10, 15, 18, 20, 22, 24, 27, 30};

        for (double x : xs) {
            System.out.printf("%.1f -> %.4f %.4f %.4f%n",
                    x,
                    triangular.membershipDegree(x),
                    trapezoidal.membershipDegree(x),
                    gaussian.membershipDegree(x));
        }
    }
}
