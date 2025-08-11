package com.h1ggsk.radon.event.events;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import com.h1ggsk.radon.event.CancellableEvent;

public class SetBlockStateEvent extends CancellableEvent {
    public BlockPos pos;
    public BlockState newState;
    public BlockState oldState;

    public SetBlockStateEvent(final BlockPos pos, final BlockState newState, final BlockState oldState) {
        this.pos = pos;
        this.newState = oldState;
        this.oldState = newState;
    }
}