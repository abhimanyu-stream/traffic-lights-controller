package com.example.trafficlight.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TimingHistory {
    private final List<StateTransition> transitions;
    private final int maxSize;
    
    public TimingHistory() {
        this(1000);
    }
    
    public TimingHistory(int maxSize) {
        this.transitions = new ArrayList<>();
        this.maxSize = maxSize;
    }
    
    public void record(StateTransition transition) {
        transitions.add(transition);
        if (transitions.size() > maxSize) {
            transitions.remove(0);
        }
    }
    
    public List<StateTransition> query(Direction direction, Instant start, Instant end) {
        return transitions.stream()
            .filter(t -> direction == null || t.direction().equals(direction))
            .filter(t -> start == null || !t.timestamp().isBefore(start))
            .filter(t -> end == null || !t.timestamp().isAfter(end))
            .toList();
    }
}
