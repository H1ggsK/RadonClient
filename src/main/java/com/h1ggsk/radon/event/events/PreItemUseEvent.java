package com.h1ggsk.radon.event.events;

import com.h1ggsk.radon.event.CancellableEvent;

public class PreItemUseEvent extends CancellableEvent {
    public int cooldown;

    public PreItemUseEvent(final int cooldown) {
        this.cooldown = cooldown;
    }
}