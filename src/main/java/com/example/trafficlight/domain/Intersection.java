package com.example.trafficlight.domain;

import com.example.trafficlight.exception.ConflictException;
import com.example.trafficlight.exception.ConfigurationException;
import jakarta.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
public class Intersection {
    @Id
    private String id;
    
    @Version
    private Long version;
    
    @Enumerated(EnumType.STRING)
    private OperationMode mode;
    
    private boolean isConfigured;
    
    @OneToMany(cascade = CascadeType.ALL)
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Direction, TrafficLight> lights;
    
    @ElementCollection
    private List<ConflictRule> conflictRules;
    
    @Embedded
    private TimingHistory timingHistory;
    
    public Intersection() {
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    public OperationMode getMode() {
        return mode;
    }
    
    public void setMode(OperationMode mode) {
        this.mode = mode;
    }
    
    public boolean isConfigured() {
        return isConfigured;
    }
    
    public void setConfigured(boolean configured) {
        isConfigured = configured;
    }
    
    public Map<Direction, TrafficLight> getLights() {
        return lights;
    }
    
    public void setLights(Map<Direction, TrafficLight> lights) {
        this.lights = lights;
    }
    
    public List<ConflictRule> getConflictRules() {
        return conflictRules;
    }
    
    public void setConflictRules(List<ConflictRule> conflictRules) {
        this.conflictRules = conflictRules;
    }
    
    public TimingHistory getTimingHistory() {
        return timingHistory;
    }
    
    public void setTimingHistory(TimingHistory timingHistory) {
        this.timingHistory = timingHistory;
    }
    
    public void transitionLight(Direction direction, LightState newState) {
        if (mode == OperationMode.PAUSED) {
            throw new com.example.trafficlight.exception.InvalidOperationException("Cannot transition while paused");
        }
        
        validateNoConflict(direction, newState);
        
        TrafficLight light = lights.get(direction);
        LightState oldState = light.getCurrentState();
        
        light.setState(newState);
        timingHistory.record(new StateTransition(direction, oldState, newState, java.time.Instant.now()));
    }
    
    private void validateNoConflict(Direction direction, LightState newState) {
        if (newState != LightState.GREEN) {
            return;
        }
        
        for (ConflictRule rule : conflictRules) {
            if (rule.involves(direction)) {
                Direction conflicting = rule.getConflicting(direction);
                TrafficLight conflictingLight = lights.get(conflicting);
                if (conflictingLight.getCurrentState() == LightState.GREEN) {
                    throw new ConflictException(
                        "Cannot set " + direction + " to GREEN while " + conflicting + " is GREEN",
                        direction,
                        conflicting
                    );
                }
            }
        }
    }
    
    public void validateSequence(Direction direction, LightSequence sequence) {
        java.util.Map<Direction, LightState> simulatedStates = new java.util.HashMap<>();
        for (Direction dir : lights.keySet()) {
            simulatedStates.put(dir, lights.get(dir).getCurrentState());
        }
        
        for (SequenceStep step : sequence.steps()) {
            simulatedStates.put(direction, step.state());
            if (step.state() == LightState.GREEN) {
                for (ConflictRule rule : conflictRules) {
                    if (rule.involves(direction)) {
                        Direction conflicting = rule.getConflicting(direction);
                        if (simulatedStates.get(conflicting) == LightState.GREEN) {
                            throw new ConflictException(
                                "Sequence would create conflict between " + direction + " and " + conflicting,
                                direction,
                                conflicting
                            );
                        }
                    }
                }
            }
        }
    }

    public void configureSequence(Direction direction, LightSequence sequence) {
        validateSequence(direction, sequence);
        TrafficLight light = lights.get(direction);
        light.setSequence(sequence);
    }

    public void pause() {
        this.mode = OperationMode.PAUSED;
    }

    public void resume() {
        this.mode = OperationMode.RUNNING;
    }

    public void start() {
        if (!isConfigured) {
            throw new ConfigurationException("Cannot start: sequences or conflict rules not configured");
        }
        this.mode = OperationMode.RUNNING;
    }

    public IntersectionState getCurrentState() {
        Map<Direction, LightInfo> lightStates = new java.util.HashMap<>();
        for (Map.Entry<Direction, TrafficLight> entry : lights.entrySet()) {
            TrafficLight light = entry.getValue();
            lightStates.put(entry.getKey(), new LightInfo(
                light.getCurrentState(),
                light.getTimeRemainingSeconds()
            ));
        }
        return new IntersectionState(mode, lightStates);
    }

    public List<StateTransition> getTimingHistory(Direction direction, java.time.Instant start, java.time.Instant end) {
        return timingHistory.query(direction, start, end);
    }

    public static class IntersectionState {
        private final OperationMode mode;
        private final Map<Direction, LightInfo> lights;

        public IntersectionState(OperationMode mode, Map<Direction, LightInfo> lights) {
            this.mode = mode;
            this.lights = lights;
        }

        public OperationMode getMode() {
            return mode;
        }

        public Map<Direction, LightInfo> getLights() {
            return lights;
        }
    }

    public static class LightInfo {
        private final LightState state;
        private final int timeRemainingSeconds;

        public LightInfo(LightState state, int timeRemainingSeconds) {
            this.state = state;
            this.timeRemainingSeconds = timeRemainingSeconds;
        }

        public LightState getState() {
            return state;
        }

        public int getTimeRemainingSeconds() {
            return timeRemainingSeconds;
        }
    }

}
