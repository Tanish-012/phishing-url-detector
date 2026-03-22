package com.example.phishingdetector.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanLog {
    private String id;
    private String url;
    private double phishingProbability;
    private boolean isMalicious;
    private String timestamp;
}
