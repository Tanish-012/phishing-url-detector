package com.example.phishingdetector.service;

import com.example.phishingdetector.domain.BenchmarkResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.InputStream;
import java.util.Random;

@Service
public class BenchmarkService {

    /**
     * Runs a 10-fold cross-validation on the dataset using RandomForest
     * and returns accuracy/precision/recall/F1 metrics.
     */
    public BenchmarkResult runCrossValidation() throws Exception {
        // Load the dataset
        ClassPathResource resource = new ClassPathResource("dataset.arff");
        Instances data;
        try (InputStream is = resource.getInputStream()) {
            ArffLoader loader = new ArffLoader();
            loader.setSource(is);
            data = loader.getDataSet();
        }
        data.setClassIndex(data.numAttributes() - 1);

        // Build a fresh classifier for evaluation (do not reuse the live one)
        RandomForest rf = new RandomForest();
        rf.setNumIterations(50);

        // 10-fold cross-validation
        Evaluation evaluation = new Evaluation(data);
        evaluation.crossValidateModel(rf, data, 10, new Random(42));

        // Class index: 0 = benign, 1 = phishing
        int phishingIdx = data.classAttribute().indexOfValue("phishing");
        int benignIdx   = data.classAttribute().indexOfValue("benign");

        double accuracy  = evaluation.pctCorrect() / 100.0;  // 0-1 range
        double precision = evaluation.precision(phishingIdx);
        double recall    = evaluation.recall(phishingIdx);
        double f1        = evaluation.fMeasure(phishingIdx);

        // Confusion matrix: confusionMatrix()[actual][predicted]
        double[][] cm = evaluation.confusionMatrix();
        int tp = (int) Math.round(cm[phishingIdx][phishingIdx]);
        int fn = (int) Math.round(cm[phishingIdx][benignIdx]);
        int fp = (int) Math.round(cm[benignIdx][phishingIdx]);
        int tn = (int) Math.round(cm[benignIdx][benignIdx]);

        String classifierInfo = "Random Forest (50 trees, 10-fold CV, " + data.numInstances() + " instances)";

        return new BenchmarkResult(accuracy, precision, recall, f1, tp, fp, tn, fn,
                data.numInstances(), classifierInfo);
    }
}
