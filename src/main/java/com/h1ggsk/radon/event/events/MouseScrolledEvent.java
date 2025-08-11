package com.h1ggsk.radon.event.events;

import com.h1ggsk.radon.event.CancellableEvent;

public class MouseScrolledEvent extends CancellableEvent {
    public double amount;

    public MouseScrolledEvent(final double amount) {
        this.amount = amount;
    }
}