package com.example.trafficlight.domain;

public record ConflictRule(Direction direction1, Direction direction2) {
    public boolean involves(Direction direction) {
        return direction1.equals(direction) || direction2.equals(direction);
    }
    
    public Direction getConflicting(Direction direction) {
        if (direction1.equals(direction)) return direction2;
        if (direction2.equals(direction)) return direction1;
        throw new IllegalArgumentException("Direction not in conflict rule");
    }
}
