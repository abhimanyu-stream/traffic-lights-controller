package com.example.trafficlight.exception;

import com.example.trafficlight.domain.LightState;

public class InvalidStateTransitionException extends TrafficControlException {
    private final LightState fromState;
    private final LightState toState;

    public InvalidStateTransitionException(String message, LightState fromState, LightState toState) {
        super(message);
        this.fromState = fromState;
        this.toState = toState;
    }

    public LightState getFromState() {
        return fromState;
    }

    public LightState getToState() {
        return toState;
    }
}
