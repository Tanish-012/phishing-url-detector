package com.example.phishingdetector.controller;

import com.example.phishingdetector.domain.UrlFeatures;
import com.example.phishingdetector.service.FeatureExtractorService;
import com.example.phishingdetector.service.MachineLearningService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow React frontend
@RequiredArgsConstructor
public class PhishingController {

    private final FeatureExtractorService featureExtractor;
    private final MachineLearningService machineLearningService;

    @PostMapping("/check-url")
    public ResponseEntity<PhishingResponse> checkUrl(@RequestBody PhishingRequest request) {
        try {
            // 1. Ensure absolute URL
            String url = request.getUrl();
            if(!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            // 2. Extract Features
            UrlFeatures features = featureExtractor.extractFeatures(url);

            // 3. Predict Probability
            double phishingProbability = machineLearningService.predictProbability(features);
            features.setIsPhishing(phishingProbability);

            // 4. Return result
            PhishingResponse response = new PhishingResponse();
            response.setUrl(url);
            response.setFeatures(features);
            response.setPhishingProbability(phishingProbability);
            response.setMalicious(phishingProbability > 0.5);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhishingRequest {
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhishingResponse {
        private String url;
        private UrlFeatures features;
        private double phishingProbability;
        private boolean isMalicious;
    }
}
