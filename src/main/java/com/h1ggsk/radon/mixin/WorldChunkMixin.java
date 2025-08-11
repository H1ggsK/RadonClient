package com.h1ggsk.radon.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.h1ggsk.radon.event.events.SetBlockStateEvent;
import com.h1ggsk.radon.manager.EventManager;

@Mixin({WorldChunk.class})
public class WorldChunkMixin {
    @Shadow
    @Final
    World world;

    @Inject(method = {"setBlockState"}, at = {@At("TAIL")})
    private void onSetBlockState(final BlockPos pos, final BlockState state, final boolean moved, final CallbackInfoReturnable<BlockState> cir) {
        if (this.world.isClient) {
            EventManager.b(new SetBlockStateEvent(pos, cir.getReturnValue(), state));
        }
    }
}