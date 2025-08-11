package com.h1ggsk.radon.module.modules.combat;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.TargetPoseEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.utils.EncryptedString;

public final class StaticHitboxes extends Module {
    public StaticHitboxes() {
        super(EncryptedString.of("Static Hitboxes"), EncryptedString.of("Expands keybindListener Player's Hitbox"), -1, Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventListener
    public void onTargetPose(final TargetPoseEvent targetPoseEvent) {
        if (this.isEnabled() && targetPoseEvent.entity instanceof PlayerEntity && !((PlayerEntity) targetPoseEvent.entity).isMainPlayer()) {
            targetPoseEvent.cir.setReturnValue(EntityPose.STANDING);
        }
    }
}
