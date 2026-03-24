package com.example.trafficlight.domain;

import java.util.List;

public record LightSequence(List<SequenceStep> steps) {
    public LightSequence {
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("Sequence must have at least one step");
        }
        steps = List.copyOf(steps);
    }
    
    public SequenceStep getStep(int index) {
        return steps.get(index % steps.size());
    }
}
