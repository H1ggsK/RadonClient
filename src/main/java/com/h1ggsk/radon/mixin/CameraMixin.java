package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.Radon;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import com.h1ggsk.radon.module.modules.misc.Freecam;
import com.h1ggsk.radon.module.modules.render.NoFluidOverlay;

@Mixin({Camera.class})
public class CameraMixin {
    @Unique
    private float tickDelta;

    @Inject(method = {"update"}, at = {@At("HEAD")})
    private void onUpdateHead(final BlockView area, final Entity focusedEntity, final boolean thirdPerson, final boolean inverseView, final float tickDelta, final CallbackInfo ci) {
        this.tickDelta = tickDelta;
    }

    @ModifyArgs(method = {"update"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void update(final Args args) {
        final Freecam freecam = (Freecam) Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(Freecam.class);
        if (freecam.isEnabled()) {
            args.set(0, (Object) freecam.getInterpolatedX(this.tickDelta));
            args.set(1, (Object) freecam.getInterpolatedY(this.tickDelta));
            args.set(2, (Object) freecam.getInterpolatedZ(this.tickDelta));
        }
    }

    @ModifyArgs(method = {"update"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void onUpdateSetRotationArgs(final Args args) {
        final Freecam freecam = (Freecam) Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(Freecam.class);
        if (freecam.isEnabled()) {
            args.set(0, (Object) (float) freecam.getInterpolatedYaw(this.tickDelta));
            args.set(1, (Object) (float) freecam.getInterpolatedPitch(this.tickDelta));
        }
    }

    @Inject(at = @At("HEAD"),
            method = "getSubmersionType()Lnet/minecraft/block/enums/CameraSubmersionType;",
            cancellable = true)
    private void onGetSubmersionType(CallbackInfoReturnable<CameraSubmersionType> cir)
    {
        NoFluidOverlay nfo = (NoFluidOverlay) Radon.INSTANCE.getModuleManager().getModuleByClass(NoFluidOverlay.class);
        if(nfo.isEnabled() && nfo.removeWater.getValue()) {
            cir.setReturnValue(CameraSubmersionType.NONE);
        }
    }
}