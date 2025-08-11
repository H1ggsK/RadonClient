package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.Radon;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.h1ggsk.radon.module.modules.misc.AntiConsume;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin {
    @Shadow
    private int life;

    @Shadow
    private int lifeTime;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        FireworkRocketEntity firework = ((FireworkRocketEntity) (Object) this);
        AntiConsume antiConsume = (AntiConsume) Radon.INSTANCE.getModuleManager().getModuleByClass(AntiConsume.class);
        if (antiConsume.isFirework(firework) && this.life > this.lifeTime) {
            firework.discard();
        }
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void onEntityHit(EntityHitResult entityHitResult, CallbackInfo info) {
        FireworkRocketEntity firework = ((FireworkRocketEntity) (Object) this);
        AntiConsume antiConsume = (AntiConsume) Radon.INSTANCE.getModuleManager().getModuleByClass(AntiConsume.class);
        if (antiConsume.isFirework(firework)) {
            firework.discard();
            info.cancel();
        }
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"), cancellable = true)
    private void onBlockHit(BlockHitResult blockHitResult, CallbackInfo info) {
        FireworkRocketEntity firework = ((FireworkRocketEntity) (Object) this);
        AntiConsume antiConsume = (AntiConsume) Radon.INSTANCE.getModuleManager().getModuleByClass(AntiConsume.class);
        if (antiConsume.isFirework(firework)) {
            firework.discard();
            info.cancel();
        }
    }
}