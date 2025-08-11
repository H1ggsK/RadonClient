package com.h1ggsk.radon.event.events;

import com.h1ggsk.radon.event.CancellableEvent;

public class MouseButtonEvent extends CancellableEvent {
    public int button;
    public int actions;
    public long window;

    public MouseButtonEvent(final int button, final long window, final int actions) {
        this.button = button;
        this.window = window;
        this.actions = actions;
    }
}