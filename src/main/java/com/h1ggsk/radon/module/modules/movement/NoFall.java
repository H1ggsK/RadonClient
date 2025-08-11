package com.h1ggsk.radon.module.modules.movement;

import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.PacketSendEvent;
import com.h1ggsk.radon.mixin.PlayerMoveC2SPacketAccessor;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.utils.EncryptedString;

public final class NoFall extends Module {

    public NoFall() {
        super(EncryptedString.of("NoFall"), EncryptedString.of("Cancels all fall damage"), -1, Category.MOVEMENT);
    }

    @EventListener
    private void onSendPacket(PacketSendEvent event) {
        if (mc == null) return;
        if (mc.player == null) return;
        if (mc.player.getAbilities().creativeMode) return;

        if (mc.player.isGliding()) return;
        if (mc.player.getVelocity().y > -0.5) return;
        ((PlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(true);
    }
}