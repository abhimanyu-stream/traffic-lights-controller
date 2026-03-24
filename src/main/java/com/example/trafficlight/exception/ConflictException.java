package com.example.trafficlight.exception;

import com.example.trafficlight.domain.Direction;

public class ConflictException extends TrafficControlException {
    private final Direction direction1;
    private final Direction direction2;

    public ConflictException(String message, Direction direction1, Direction direction2) {
        super(message);
        this.direction1 = direction1;
        this.direction2 = direction2;
    }

    public Direction getDirection1() {
        return direction1;
    }

    public Direction getDirection2() {
        return direction2;
    }
}
