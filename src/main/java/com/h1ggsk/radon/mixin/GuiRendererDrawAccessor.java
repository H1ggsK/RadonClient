package com.h1ggsk.radon.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.gui.render.GuiRenderer$Draw")
public interface GuiRendererDrawAccessor {
    @Accessor("pipeline")
    RenderPipeline radon$pipeline();

    @Accessor("indexCount")
    int radon$indexCount();
}
