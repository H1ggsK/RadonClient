package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.Radon;
import com.h1ggsk.radon.module.modules.combat.Hitbox;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {
    @Inject(method = {"renderHitbox"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER))
    private static void onRenderHitboxEditBox(MatrixStack matrices, VertexConsumer vertexConsumer, EntityHitbox hitbox, CallbackInfo ci) {
        final Hitbox hitboxes = (Hitbox) Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(Hitbox.class);
        if (!hitboxes.isEnabled()) return;

        float ex = (float) (hitboxes.getHitboxExpansion() + 1);
        matrices.scale(ex, ex, ex);
    }
}