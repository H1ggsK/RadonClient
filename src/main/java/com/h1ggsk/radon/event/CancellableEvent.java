package com.h1ggsk.radon.event;

public abstract class CancellableEvent implements Event {
    private boolean cancelled;

    public CancellableEvent() {
        this.cancelled = false;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }
}