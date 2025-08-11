package com.h1ggsk.radon.event.events;

import com.h1ggsk.radon.event.CancellableEvent;

public class PlayerRespawnEvent extends CancellableEvent {
    private static final PlayerRespawnEvent INSTANCE = new PlayerRespawnEvent();

    public static PlayerRespawnEvent get() {
        return INSTANCE;
    }
}