package com.example.phishingdetector.service;

import com.example.phishingdetector.domain.UrlFeatures;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;

@Service
public class MachineLearningService {

    private RandomForest classifier;
    private Instances datasetStructure;

    @PostConstruct
    public void init() throws Exception {
        // Define attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("urlLength"));
        attributes.add(new Attribute("numSuspiciousChars"));
        attributes.add(new Attribute("numLexicalKeywords"));
        attributes.add(new Attribute("hasSuspiciousIframes"));
        attributes.add(new Attribute("externalLinkRatio"));
        attributes.add(new Attribute("hasFakeLoginForm"));

        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("benign");
        classValues.add("phishing");
        Attribute classAttribute = new Attribute("class", classValues);
        attributes.add(classAttribute);

        // Create dataset structure
        datasetStructure = new Instances("PhishingDataset", attributes, 0);
        datasetStructure.setClassIndex(datasetStructure.numAttributes() - 1);

        // Generate synthetic training dataset representing heuristic rules
        // In a production system, this would load from an ARFF file or DB
        Instances trainingData = new Instances(datasetStructure, 100);

        // Benign Examples
        trainingData.add(createInstance(25.0, 0.0, 0.0, 0.0, 0.1, 0.0, "benign"));
        trainingData.add(createInstance(30.0, 1.0, 0.0, 0.0, 0.2, 0.0, "benign"));
        trainingData.add(createInstance(15.0, 0.0, 0.0, 0.0, 0.05, 0.0, "benign"));
        trainingData.add(createInstance(45.0, 2.0, 0.0, 0.0, 0.3, 0.0, "benign"));

        // Phishing Examples (high suspicious chars, keywords, iframes, fake logins)
        trainingData.add(createInstance(120.0, 5.0, 2.0, 1.0, 0.8, 1.0, "phishing"));
        trainingData.add(createInstance(85.0, 3.0, 3.0, 0.0, 0.9, 1.0, "phishing"));
        trainingData.add(createInstance(60.0, 4.0, 1.0, 1.0, 0.7, 0.0, "phishing"));
        trainingData.add(createInstance(150.0, 6.0, 2.0, 1.0, 0.95, 1.0, "phishing"));

        // Train the Random Forest
        classifier = new RandomForest();
        classifier.setNumIterations(10); // small number for fast MVP training
        classifier.buildClassifier(trainingData);
        System.out.println("Weka Random Forest model successfully trained.");
    }

    private DenseInstance createInstance(double length, double susp, double keywords, double iframe, double ratio, double fakeLogin, String label) {
        DenseInstance inst = new DenseInstance(7);
        inst.setValue(datasetStructure.attribute(0), length);
        inst.setValue(datasetStructure.attribute(1), susp);
        inst.setValue(datasetStructure.attribute(2), keywords);
        inst.setValue(datasetStructure.attribute(3), iframe);
        inst.setValue(datasetStructure.attribute(4), ratio);
        inst.setValue(datasetStructure.attribute(5), fakeLogin);
        inst.setValue(datasetStructure.attribute(6), label);
        return inst;
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
