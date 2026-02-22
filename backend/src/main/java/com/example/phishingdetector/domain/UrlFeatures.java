package com.example.phishingdetector.domain;

import lombok.Data;

@Data
public class UrlFeatures {
    private String url;
    private double urlLength;
    private double numSuspiciousChars;
    private double numLexicalKeywords;
    private double hasSuspiciousIframes;
    private double externalLinkRatio;
    private double hasFakeLoginForm;
    // Target label: 0 for Benign, 1 for Phishing
    private double isPhishing; 
}
