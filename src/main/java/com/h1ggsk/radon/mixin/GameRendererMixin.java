package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.Radon;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.h1ggsk.radon.event.events.Render3DEvent;
import com.h1ggsk.radon.manager.EventManager;
import com.h1ggsk.radon.module.modules.misc.Freecam;

@Mixin({GameRenderer.class})
public abstract class GameRendererMixin {
    @Shadow protected abstract float getFov(Camera camera, float tickProgress, boolean changingFov);

    @Shadow public abstract Matrix4f getBasicProjectionMatrix(float fov);

    @Shadow @Final private Camera camera;

    @Inject(method = {"renderWorld"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1)})
    private void onWorldRender(final RenderTickCounter rtc, final CallbackInfo ci, @Local MatrixStack matrixStack, @Local(ordinal = 1) Matrix4f matrix4f2, @Local(ordinal = 1) float tickDelta) {
        EventManager.throwEvent(new Render3DEvent(new MatrixStack(), this.getBasicProjectionMatrix(this.getFov(this.camera, rtc.getTickProgress(true), true)), rtc.getTickProgress(true)));
    }

    @Inject(method = {"shouldRenderBlockOutline"}, at = {@At("HEAD")}, cancellable = true)
    private void onShouldRenderBlockOutline(final CallbackInfoReturnable<Boolean> cir) {
        if (Radon.INSTANCE.getModuleManager().getModuleByClass(Freecam.class).isEnabled()) {
            cir.setReturnValue(false);
        }
    }
}