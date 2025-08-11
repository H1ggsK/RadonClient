package com.h1ggsk.radon.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import com.h1ggsk.radon.Radon;
import com.h1ggsk.radon.module.modules.render.NoFluidOverlay;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin
{
	@ModifyConstant(
		method = "renderFireOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
		constant = @Constant(floatValue = -0.3F))
	private static float getFireOffset(float original)
	{
		NoFluidOverlay nfo = (NoFluidOverlay) Radon.INSTANCE.getModuleManager().getModuleByClass(NoFluidOverlay.class);
		return original - nfo.getOverlayOffset();
	}
	
	@Inject(at = @At("HEAD"),
		method = "renderUnderwaterOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
		cancellable = true)
	private static void onRenderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci)
	{
		NoFluidOverlay nfo = (NoFluidOverlay) Radon.INSTANCE.getModuleManager().getModuleByClass(NoFluidOverlay.class);
		if(nfo.isEnabled() && nfo.removeWater.getValue())
			ci.cancel();
	}
}