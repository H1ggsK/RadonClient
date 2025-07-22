package skid.krypton.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import skid.krypton.Krypton;
import skid.krypton.module.modules.render.NoFluidOverlay;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin
{
	@ModifyConstant(
		method = "renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V",
		constant = @Constant(floatValue = -0.3F))
	private static float getFireOffset(float original)
	{
		NoFluidOverlay nfo = (NoFluidOverlay) Krypton.INSTANCE.getModuleManager().getModuleByClass(NoFluidOverlay.class);
		return original - nfo.getOverlayOffset();
	}
	
	@Inject(at = @At("HEAD"),
		method = "renderUnderwaterOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V",
		cancellable = true)
	private static void onRenderUnderwaterOverlay(MinecraftClient client,
		MatrixStack matrices, CallbackInfo ci)
	{
		NoFluidOverlay nfo = (NoFluidOverlay) Krypton.INSTANCE.getModuleManager().getModuleByClass(NoFluidOverlay.class);
		if(nfo.isEnabled() && nfo.removeWater.getValue())
			ci.cancel();
	}
}