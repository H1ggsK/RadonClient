package skid.krypton.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skid.krypton.event.events.KeyEvent;
import skid.krypton.manager.EventManager;

import static skid.krypton.Krypton.mc;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Unique
    private static boolean eatNextChar = false;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return;

        KeyEvent evt = new KeyEvent(key, window, action);
        EventManager.b(evt);
        if (evt.isCancelled()) {
            ci.cancel();
            if (action == GLFW.GLFW_PRESS && mc.currentScreen instanceof ChatScreen) {
                eatNextChar = true;
            }
        }
    }

    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    private void onChar(long window, int codepoint, int modifiers, CallbackInfo ci) {
        if (eatNextChar) {
            eatNextChar = false;
            ci.cancel();
        }
    }
}
