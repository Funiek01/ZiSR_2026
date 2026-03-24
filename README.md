# ZiSR_2026

Simple Java program for a fuzzy classifier built from the Iris dataset.

What it does:
- loads `data/iris.data`,
- calculates mean, median, standard deviation, min and max for each feature in each class,
- builds one fuzzy set per feature and class,
- classifies a vector by summing membership values.

Run from IntelliJ or from command line:

```powershell
java -cp out\production\FuzzySets Main
java -cp out\production\FuzzySets Main 5.1 3.5 1.4 0.2
```
