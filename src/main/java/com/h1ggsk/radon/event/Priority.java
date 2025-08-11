package com.h1ggsk.radon.event;

public enum Priority {
    HIGHEST( 0),
    HIGH(1),
    NORMAL(2),
    LOW(3),
    LOWEST(4);

    private final int value;

    Priority(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}