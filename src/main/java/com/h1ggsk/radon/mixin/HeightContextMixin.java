package com.h1ggsk.radon.mixin;

import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({HeightContext.class})
public abstract class HeightContextMixin {
    @Redirect(method = {"<init>"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getMinimumY()I"))
    private int onMinY(final ChunkGenerator cg) {
        return cg == null ? -9999999 : cg.getMinimumY();
    }

    @Redirect(method = {"<init>"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getWorldHeight()I"))
    private int onHeight(final ChunkGenerator cg) {
        return cg == null ? 100000000 : cg.getWorldHeight();
    }
}