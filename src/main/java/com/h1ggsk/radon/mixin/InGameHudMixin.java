package com.h1ggsk.radon.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.h1ggsk.radon.event.events.Render2DEvent;
import com.h1ggsk.radon.manager.EventManager;

@Mixin({InGameHud.class})
public class InGameHudMixin {
    @Inject(method = {"render"}, at = {@At("HEAD")})
    private void onRenderHud(final DrawContext ctx, final RenderTickCounter rtc, final CallbackInfo ci) {
        EventManager.b(new Render2DEvent(ctx, rtc.getTickProgress(true)));
    }
}