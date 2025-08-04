package skid.krypton.event.events;

import net.minecraft.client.gui.screen.Screen;
import skid.krypton.event.CancellableEvent;

public class SetScreenEvent extends CancellableEvent {
    public Screen screen;

    public SetScreenEvent(final Screen screen) {
        try {
            this.screen = screen;
        } catch (Exception ignored) {}
    }
}