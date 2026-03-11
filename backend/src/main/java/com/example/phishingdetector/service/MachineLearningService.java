package com.example.phishingdetector.service;

import com.example.phishingdetector.domain.UrlFeatures;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Service
public class MachineLearningService {

    private RandomForest classifier;
    private Instances datasetStructure;

    @PostConstruct
    public void init() throws Exception {
        // Load dataset from ARFF file
        ClassPathResource resource = new ClassPathResource("dataset.arff");
        try (InputStream is = resource.getInputStream()) {
            ArffLoader loader = new ArffLoader();
            loader.setSource(is);
            Instances trainingData = loader.getDataSet();
            
            // Set class index to the last attribute
            trainingData.setClassIndex(trainingData.numAttributes() - 1);
            
            // Save structure for prediction
            datasetStructure = new Instances(trainingData, 0);

            // Train the Random Forest
            classifier = new RandomForest();
            classifier.setNumIterations(50); // increased iterations for better accuracy now that we have more data
            classifier.buildClassifier(trainingData);
            System.out.println("Weka Random Forest model successfully trained on " + trainingData.numInstances() + " instances.");
        } catch (Exception e) {
            System.err.println("Warning: Could not load dataset.arff. Model not trained. EnsureDatasetGeneratorTest has been run.");
            e.printStackTrace();
        }
    }

    public double predictProbability(UrlFeatures features) throws Exception {
        // Create instance for prediction
        DenseInstance inst = new DenseInstance(7);
        inst.setDataset(datasetStructure);
        inst.setValue(0, features.getUrlLength());
        inst.setValue(1, features.getNumSuspiciousChars());
        inst.setValue(2, features.getNumLexicalKeywords());
        inst.setValue(3, features.getHasSuspiciousIframes());
        inst.setValue(4, features.getExternalLinkRatio());
        inst.setValue(5, features.getHasFakeLoginForm());
        
        // Return the probability of class index 1 ("phishing")
        double[] distribution = classifier.distributionForInstance(inst);
        return distribution[1];
    }
}
