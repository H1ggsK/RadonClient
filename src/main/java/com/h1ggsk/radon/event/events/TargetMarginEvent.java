package com.h1ggsk.radon.event.events;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.h1ggsk.radon.event.CancellableEvent;

public class TargetMarginEvent extends CancellableEvent {
    public Entity entity;
    public CallbackInfoReturnable<Float> cir;

    public TargetMarginEvent(final Entity entity, final CallbackInfoReturnable<Float> cir) {
        this.entity = entity;
        this.cir = cir;
    }
}