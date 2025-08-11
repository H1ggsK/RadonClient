package com.h1ggsk.radon.event.events;

import net.minecraft.client.util.Window;
import com.h1ggsk.radon.event.CancellableEvent;

public class ResolutionChangedEvent extends CancellableEvent {
    public Window window;

    public ResolutionChangedEvent(final Window window) {
        this.window = window;
    }
}