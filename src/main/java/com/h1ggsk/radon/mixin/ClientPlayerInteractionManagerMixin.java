package com.h1ggsk.radon.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.h1ggsk.radon.event.events.AttackBlockEvent;
import com.h1ggsk.radon.event.events.InteractItemEvent;
import com.h1ggsk.radon.manager.EventManager;

@Mixin({ClientPlayerInteractionManager.class})
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = {"attackBlock"}, at = {@At("HEAD")})
    private void onAttackBlock(final BlockPos pos, final Direction dir, final CallbackInfoReturnable<Boolean> cir) {
        EventManager.throwEvent(new AttackBlockEvent(pos, dir));
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void onInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        InteractItemEvent event = InteractItemEvent.get(hand);
        EventManager.throwEvent(event);
        if (event.toReturn != null) {
            cir.setReturnValue(event.toReturn);
        }
    }
}