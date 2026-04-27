@echo off
cd /d "%~dp0"
java -cp "bin" com.fss.MainDiabetesClassifier diabetes.data.csv
