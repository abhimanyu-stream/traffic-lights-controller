package com.example.trafficlight.domain;

import java.time.Instant;

public record StateTransition(
    Direction direction,
    LightState fromState,
    LightState toState,
    Instant timestamp
) {}
