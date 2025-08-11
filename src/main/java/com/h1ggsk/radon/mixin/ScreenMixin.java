package com.h1ggsk.radon.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.h1ggsk.radon.gui.ClickGUI;

@Mixin({Screen.class})
public class ScreenMixin {
    @Shadow
    @NotNull
    protected MinecraftClient client;

    @Inject(method = {"renderBackground"}, at = {@At("HEAD")}, cancellable = true)
    private void dontRenderBackground(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (this.client.currentScreen instanceof ClickGUI) {
            ci.cancel();
        }
    }
}