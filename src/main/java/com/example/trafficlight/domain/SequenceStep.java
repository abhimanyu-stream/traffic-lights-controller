package com.example.trafficlight.domain;

public record SequenceStep(LightState state, int durationSeconds) {
    public SequenceStep {
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
    }
}
