package com.h1ggsk.radon.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface MinecraftClientAccessor {
    @Invoker
    void invokeDoItemUse();
}