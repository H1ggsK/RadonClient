package skid.krypton.module.modules.movement;

import skid.krypton.event.EventListener;
import skid.krypton.event.events.PacketSendEvent;
import skid.krypton.mixin.PlayerMoveC2SPacketAccessor;
import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.utils.EncryptedString;

public final class NoFall extends Module {

    public NoFall() {
        super(EncryptedString.of("NoFall"), EncryptedString.of("Cancels all fall damage"), -1, Category.MOVEMENT);
    }

    @EventListener
    private void onSendPacket(PacketSendEvent event) {
        if (mc == null) return;
        if (mc.player.getAbilities().creativeMode) return;

        if (mc.player.isFallFlying()) return;
        if (mc.player.getVelocity().y > -0.5) return;
        ((PlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(true);
    }
}