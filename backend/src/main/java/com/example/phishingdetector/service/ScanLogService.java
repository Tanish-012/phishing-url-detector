package com.example.phishingdetector.service;

import com.example.phishingdetector.domain.ScanLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ScanLogService {

    private static final String LOG_FILE = "scan_log.json";
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private final List<ScanLog> logEntries = new ArrayList<>();

    @PostConstruct
    public void init() {
        File file = new File(LOG_FILE);
        if (file.exists()) {
            try {
                List<ScanLog> loaded = objectMapper.readValue(file,
                        new TypeReference<List<ScanLog>>() {});
                logEntries.addAll(loaded);
                System.out.println("Loaded " + logEntries.size() + " scan log entries from " + LOG_FILE);
            } catch (IOException e) {
                System.err.println("Warning: Could not load scan_log.json – starting fresh. " + e.getMessage());
            }
        }
    }

    public synchronized void save(ScanLog entry) {
        logEntries.add(0, entry); // newest first
        persist();
    }

    public List<ScanLog> getAll() {
        return Collections.unmodifiableList(logEntries);
    }

    private void persist() {
        try {
            objectMapper.writeValue(new File(LOG_FILE), logEntries);
        } catch (IOException e) {
            System.err.println("Error persisting scan log: " + e.getMessage());
        }
    }
}
