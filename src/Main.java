/**
 * Demonstration of the fuzzy set class hierarchy.
 *
 * Shows the usage of FuzzySet (abstract superclass), TriangularFuzzySet,
 * TrapezoidalFuzzySet, and GaussianFuzzySet.
 */
public class Main {

    public static void main(String[] args) {
        // Triangular fuzzy set: "medium" temperature with peak at 20
        FuzzySet triangular = new TriangularFuzzySet("medium_temperature", 10, 20, 30);

        // Trapezoidal fuzzy set: "comfortable" temperature plateau between 18 and 24
        FuzzySet trapezoidal = new TrapezoidalFuzzySet("comfortable_temperature", 15, 18, 24, 27);

        // Gaussian fuzzy set: "around 20 degrees" with sigma=3
        FuzzySet gaussian = new GaussianFuzzySet("around_20_degrees", 20, 3);

        System.out.println("=== Fuzzy Set Demonstration ===\n");

        System.out.println(triangular);
        System.out.println(trapezoidal);
        System.out.println(gaussian);

        System.out.println("\n--- Membership degrees for x in {10, 15, 18, 20, 22, 24, 27, 30} ---\n");
        double[] testValues = {10, 15, 18, 20, 22, 24, 27, 30};

        System.out.printf("%-6s | %-20s | %-24s | %-20s%n",
                "x", triangular.getName(), trapezoidal.getName(), gaussian.getName());
        System.out.println("-".repeat(80));

        for (double x : testValues) {
            System.out.printf("%-6.1f | %-20.4f | %-24.4f | %-20.4f%n",
                    x,
                    triangular.membershipDegree(x),
                    trapezoidal.membershipDegree(x),
                    gaussian.membershipDegree(x));
        }
    }
}
