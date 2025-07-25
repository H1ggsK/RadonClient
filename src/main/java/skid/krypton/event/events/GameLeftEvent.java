package skid.krypton.event.events;

import skid.krypton.event.CancellableEvent;

public class GameLeftEvent extends CancellableEvent {
    private static final GameLeftEvent INSTANCE = new GameLeftEvent();

    public static GameLeftEvent get() {
        return INSTANCE;
    }
}