package com.h1ggsk.radon.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ClientPlayerInteractionManager.class})
public interface ClientPlayerInteractionManagerAccessor {
    @Invoker("syncSelectedSlot")
    void syncSlot();
}