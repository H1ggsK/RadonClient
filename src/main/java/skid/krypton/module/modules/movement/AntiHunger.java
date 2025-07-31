package skid.krypton.module.modules.movement;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import skid.krypton.event.EventListener;
import skid.krypton.event.events.PacketSendEvent;
import skid.krypton.event.events.SendMovementPacketsEvent;
import skid.krypton.mixin.PlayerMoveC2SPacketAccessor;
import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.module.setting.BooleanSetting;
import skid.krypton.utils.EncryptedString;

public final class AntiHunger extends Module {
    private final BooleanSetting sprint = new BooleanSetting("Spoof Sprint", true);
    private final BooleanSetting onGround = new BooleanSetting("Spoof onGround", true);
    private boolean lastOnGround, ignorePacket;

    public AntiHunger() {
        super(EncryptedString.of("Anti-Hunger"), EncryptedString.of("Reduces (does NOT remove) hunger consumption."), -1, Category.MOVEMENT);
        this.addSettings(this.sprint, this.onGround);
    }

    @Override
    public void onEnable() {
        if (mc != null && mc.player != null) {
            lastOnGround = mc.player.isOnGround();
        }
    }

    @EventListener
    private void onSendPacket(PacketSendEvent event) {
        if (mc == null || mc.player == null) return;

        if (ignorePacket && event.packet instanceof PlayerMoveC2SPacket) {
            ignorePacket = false;
            return;
        }

        if (mc.player.hasVehicle() || mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) return;

        if (event.packet instanceof ClientCommandC2SPacket packet && sprint.getValue()) {
            if (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) event.cancel();
        }

        if (event.packet instanceof PlayerMoveC2SPacket packet && onGround.getValue() && mc.player.isOnGround() && mc.player.fallDistance <= 0.0 && !mc.interactionManager.isBreakingBlock()) {
            ((PlayerMoveC2SPacketAccessor) packet).setOnGround(false);
        }
    }

    @EventListener
    private void onTick(SendMovementPacketsEvent event) {
        if (mc == null || mc.player == null) return;

        if (mc.player.isOnGround() && !lastOnGround && onGround.getValue()) {
            ignorePacket = true; // prevents you from not taking fall damage
        }

        lastOnGround = mc.player.isOnGround();
    }
}