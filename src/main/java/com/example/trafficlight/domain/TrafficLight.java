package com.example.trafficlight.domain;

import com.example.trafficlight.exception.InvalidStateTransitionException;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class TrafficLight {
    @Id
    @GeneratedValue
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private Direction direction;
    
    @Enumerated(EnumType.STRING)
    private LightState currentState;
    
    private int currentStepIndex;
    
    private Instant stateStartTime;
    
    @Embedded
    private LightSequence sequence;
    
    public TrafficLight() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public LightState getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(LightState currentState) {
        this.currentState = currentState;
    }
    
    public int getCurrentStepIndex() {
        return currentStepIndex;
    }
    
    public void setCurrentStepIndex(int currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }
    
    public Instant getStateStartTime() {
        return stateStartTime;
    }
    
    public void setStateStartTime(Instant stateStartTime) {
        this.stateStartTime = stateStartTime;
    }
    
    public LightSequence getSequence() {
        return sequence;
    }
    
    public void setSequence(LightSequence sequence) {
        this.sequence = sequence;
    }
    
    public void setState(LightState newState) {
        if (currentState == LightState.GREEN && newState == LightState.RED) {
            throw new InvalidStateTransitionException(
                "Cannot transition directly from GREEN to RED without YELLOW intermediate state",
                currentState,
                newState
            );
        }
        this.currentState = newState;
    }
    
    public boolean shouldTransition() {
        if (sequence == null || stateStartTime == null) {
            return false;
        }
        SequenceStep currentStep = sequence.getStep(currentStepIndex);
        long elapsedSeconds = Instant.now().getEpochSecond() - stateStartTime.getEpochSecond();
        return elapsedSeconds >= currentStep.durationSeconds();
    }
    
    public SequenceStep getNextStep() {
        return sequence.getStep(currentStepIndex + 1);
    }
    
    public void advanceToNextStep() {
        currentStepIndex++;
    }
    
    public int getTimeRemainingSeconds() {
        if (sequence == null || stateStartTime == null) {
            return 0;
        }
        SequenceStep currentStep = sequence.getStep(currentStepIndex);
        long elapsedSeconds = Instant.now().getEpochSecond() - stateStartTime.getEpochSecond();
        int remaining = currentStep.durationSeconds() - (int) elapsedSeconds;
        return Math.max(0, remaining);
    }
}
