package skid.krypton.event.events;

import skid.krypton.event.CancellableEvent;

public class GameJoinedEvent extends CancellableEvent {
    private static final GameJoinedEvent INSTANCE = new GameJoinedEvent();

    public static GameJoinedEvent get() {
        return INSTANCE;
    }
}