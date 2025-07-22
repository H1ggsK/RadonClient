package skid.krypton.event.events;

import skid.krypton.event.CancellableEvent;

public class PlayerRespawnEvent extends CancellableEvent {
    private static final PlayerRespawnEvent INSTANCE = new PlayerRespawnEvent();

    public static PlayerRespawnEvent get() {
        return INSTANCE;
    }
}