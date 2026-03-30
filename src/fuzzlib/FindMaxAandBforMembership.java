package fuzzlib;

import fuzzlib.creators.OperationCreator;
import fuzzlib.norms.Norm;
import fuzzlib.norms.TNorm;

class FindMaxAandBforMembership implements FindMaxMembership {

    private final short compositionType;

    public FindMaxAandBforMembership(short compositionType) {
        this.compositionType = compositionType;
    }

    public double findMaxAandBforMembership(double membership, FuzzySet trA, FuzzySet trB, TNorm tnorm, double minDY,
            double minDX) {
        Norm composition = OperationCreator.newNorm(compositionType);
        double step = minDX > 0.0 ? minDX : 0.01;
        double tolerance = step;
        double max = 0.0;

        for (double a = 0.0; a <= 1.0; a += step) {
            for (double b = 0.0; b <= 1.0; b += step) {
                double composed = composition.calc(a, b);
                if (Math.abs(composed - membership) <= tolerance) {
                    double candidate = tnorm.calc(trA.getMembership(a), trB.getMembership(b));
                    if (candidate > max) {
                        max = candidate;
                    }
                }
            }
        }

        return max;
    }
}
