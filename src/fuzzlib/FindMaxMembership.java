package fuzzlib;

import fuzzlib.norms.TNorm;

interface FindMaxMembership {
    double findMaxAandBforMembership(double membership, FuzzySet trA, FuzzySet trB, TNorm tnorm, double minDY,
            double minDX);
}
