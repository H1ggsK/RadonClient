package com.h1ggsk.radon.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
    @WrapOperation(
            method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderPass;setIndexBuffer(Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V"
            )
    )
    private void makeNonQuadsRender(RenderPass instance, GpuBuffer buffer, VertexFormat.IndexType indexType, Operation<Void> original, @Coerce GuiRendererDrawAccessor draw) {
        var pipeline = draw.radon$pipeline();

        if (!pipeline.getLocation().getNamespace().equals("radon")) {
            original.call(instance, buffer, indexType);
            return;
        }
        // seems hacky, but seems to work. look into unforeseen consequences later
        if (pipeline.getVertexFormatMode() != VertexFormat.DrawMode.QUADS) {
            var shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            buffer = shapeIndexBuffer.getIndexBuffer(draw.radon$indexCount());
            indexType = shapeIndexBuffer.getIndexType();
        }

        original.call(instance, buffer, indexType);
    }
}
