package com.h1ggsk.radon.event.events;

import com.h1ggsk.radon.event.CancellableEvent;

public class GameJoinedEvent extends CancellableEvent {
    private static final GameJoinedEvent INSTANCE = new GameJoinedEvent();

    public static GameJoinedEvent get() {
        return INSTANCE;
    }
}