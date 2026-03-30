package fuzzlib;

import fuzzlib.norms.Norm;
import fuzzlib.norms.TNorm;

class FullZadehData {

    public final FuzzySet result;
    public final FuzzySet[] F;
    public final FuzzySet[] P;
    public final int numP;
    public final FuzzySet CZ;
    public final Norm comp;
    public final Norm impl;
    public final TNorm tnorm;

    public final int[] idx;
    public final int[] start;
    public final int[] stop;

    public double max;
    public double tmp;
    public double tmpcut;
    public int tmpi;
    public int idxCZ;

    public FullZadehData(FuzzySet result, FuzzySet[] fact, FuzzySet[] premise, int numberOfPremises,
            FuzzySet conclusion, Norm comp, Norm impl, TNorm tnorm) {
        this.result = result;
        this.F = fact;
        this.P = premise;
        this.numP = numberOfPremises;
        this.CZ = conclusion;
        this.comp = comp;
        this.impl = impl;
        this.tnorm = tnorm;

        this.idx = new int[numberOfPremises];
        this.start = new int[numberOfPremises];
        this.stop = new int[numberOfPremises];

        for (int i = 0; i < numberOfPremises; i++) {
            start[i] = 0;
            stop[i] = Math.min(fact[i].getSize(), premise[i].getSize()) - 1;
        }
    }
}
