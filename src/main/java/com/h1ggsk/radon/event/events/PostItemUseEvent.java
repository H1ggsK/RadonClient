package com.h1ggsk.radon.event.events;

import com.h1ggsk.radon.event.CancellableEvent;

public class PostItemUseEvent extends CancellableEvent {
    public int cooldown;

    public PostItemUseEvent(final int cooldown) {
        this.cooldown = cooldown;
    }
}