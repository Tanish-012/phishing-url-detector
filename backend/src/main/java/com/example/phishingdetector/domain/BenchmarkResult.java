package com.example.phishingdetector.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenchmarkResult {
    private double accuracy;
    private double precision;
    private double recall;
    private double f1Score;
    private int truePositives;
    private int falsePositives;
    private int trueNegatives;
    private int falseNegatives;
    private int totalInstances;
    private String classifierInfo;
}
