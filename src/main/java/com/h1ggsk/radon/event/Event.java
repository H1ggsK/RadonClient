package com.h1ggsk.radon.event;

public interface Event {
    default boolean isCancelled() {
        return false;
    }
}