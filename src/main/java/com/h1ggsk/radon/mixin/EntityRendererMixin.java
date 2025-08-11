package com.h1ggsk.radon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.h1ggsk.radon.Main;
import com.h1ggsk.radon.module.modules.render.Fullbright;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @ModifyReturnValue(method = "getSkyLight", at = @At("RETURN"))
    private int onGetSkyLight(int original) {
        Fullbright fullbright = (Fullbright) Main.getRadon().getModuleManager().getModuleByClass(Fullbright.class);
        if (fullbright != null && fullbright.isEnabled()) {
            return Math.max(fullbright.getLuminance(LightType.SKY), original);
        }
        return original;
    }

    @ModifyReturnValue(method = "getBlockLight", at = @At("RETURN"))
    private int onGetBlockLight(int original) {
        Fullbright fullbright = (Fullbright) Main.getRadon().getModuleManager().getModuleByClass(Fullbright.class);
        if (fullbright != null && fullbright.isEnabled()) {
            return Math.max(fullbright.getLuminance(LightType.BLOCK), original);
        }
        return original;
    }
}