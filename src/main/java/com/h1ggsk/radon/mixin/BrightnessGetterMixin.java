package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.Radon;
import com.h1ggsk.radon.module.modules.render.Fullbright;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.BrightnessGetter.class)
public interface BrightnessGetterMixin {
    @ModifyVariable(method = "method_68890", at = @At(value = "STORE"), ordinal = 0)
    private static int getLightmapCoordinatesModifySkyLight(int sky) {
        Fullbright fullbright = (Fullbright) Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(Fullbright.class);
        return Math.max(fullbright.getLuminance(LightType.SKY), sky);
    }

    @ModifyVariable(method = "method_68890", at = @At(value = "STORE"), ordinal = 1)
    private static int getLightmapCoordinatesModifyBlockLight(int sky) {
        Fullbright fullbright = (Fullbright) Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(Fullbright.class);
        return Math.max(fullbright.getLuminance(LightType.BLOCK), sky);
    }
}