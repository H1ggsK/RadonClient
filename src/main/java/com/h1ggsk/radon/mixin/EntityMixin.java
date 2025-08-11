package com.h1ggsk.radon.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.h1ggsk.radon.Radon;
import com.h1ggsk.radon.event.events.TargetMarginEvent;
import com.h1ggsk.radon.event.events.TargetPoseEvent;
import com.h1ggsk.radon.manager.EventManager;
import com.h1ggsk.radon.module.modules.misc.Freecam;

@Mixin({Entity.class})
public class EntityMixin {
    @Inject(method = {"getTargetingMargin"}, at = {@At("HEAD")}, cancellable = true)
    private void onSendMovementPackets(final CallbackInfoReturnable<Float> cir) {
        EventManager.b(new TargetMarginEvent(Entity.class.cast(this), cir));
    }

    @Inject(method = {"getPose"}, at = {@At("HEAD")}, cancellable = true)
    private void onGetPose(final CallbackInfoReturnable<EntityPose> cir) {
        EventManager.b(new TargetPoseEvent(Entity.class.cast(this), cir));
    }

    @Inject(method = {"changeLookDirection"}, at = {@At("HEAD")}, cancellable = true)
    private void updateChangeLookDirection(final double cursorDeltaX, final double cursorDeltaY, final CallbackInfo ci) {
        if (Radon.mc == null) return;
        if (Radon.mc.player == null) return;
        if (Entity.class.cast(this) != Radon.mc.player) return;
        final Freecam freecam = (Freecam) Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(Freecam.class);

        if (freecam.isEnabled()) {
            freecam.updateRotation(cursorDeltaX * 0.15, cursorDeltaY * 0.15);
            ci.cancel();
        }
    }
}