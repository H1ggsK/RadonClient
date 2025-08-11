package com.h1ggsk.radon.module.modules.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.PacketSendEvent;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.mixin.PlayerMoveC2SPacketAccessor;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.ModeSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public final class Flight extends Module {
    private final BooleanSetting verticalSpeedMatch = new BooleanSetting("Speed match", false);
    private final BooleanSetting noSneak = new BooleanSetting("No sneak", false);
    private final NumberSetting speed = new NumberSetting("Flight speed", 0.0, 10.0, 0.1, 0.01);
    private final ModeSetting<Mode> mode = new ModeSetting<>("Flight mode", Mode.Abilities, Mode.class);
    private final BooleanSetting noFall = new BooleanSetting("NoFall", true);
    private final ModeSetting<AntiKickMode> antiKickMode = new ModeSetting<>("Anti-kick mode", AntiKickMode.Normal, AntiKickMode.class);
    private final NumberSetting delay = new NumberSetting("Anti-kick delay", 1, 200, 20, 1);
    private final NumberSetting offTime = new NumberSetting("Anti-kick length", 1, 20, 3, 1);

    private int delayLeft = delay.getIntValue();
    private int offLeft = offTime.getIntValue();
    private double lastPacketY = Double.MAX_VALUE;
    private boolean flip;
    private float lastYaw;

    public Flight() {
        super(EncryptedString.of("Flight"), EncryptedString.of("Fly like the wind"), -1, Category.MOVEMENT);
        this.addSettings(this.verticalSpeedMatch, this.noSneak, this.speed, this.mode, this.antiKickMode, this.delay, this.offTime, this.noFall);
    }

    @Override
    public void onEnable() {
        if (mode.getValue() == Mode.Abilities && !mc.player.isSpectator()) {
            mc.player.getAbilities().flying = true;
            if (!mc.player.getAbilities().creativeMode) {
                mc.player.getAbilities().allowFlying = true;
            }
        }
    }

    @Override
    public void onDisable() {
        if (mode.getValue() == Mode.Abilities && !mc.player.isSpectator()) {
            abilitiesOff();
        }
    }

    @EventListener
    public void onTick(StartTickEvent event) {
        // Anti-kick rotation
        float currentYaw = mc.player.getYaw();
        if (mc.player.fallDistance >= 3f && currentYaw == lastYaw && mc.player.getVelocity().length() < 0.003d) {
            mc.player.setYaw(currentYaw + (flip ? 1 : -1));
            flip = !flip;
        }
        lastYaw = currentYaw;

        // Anti-kick timing
        if (delayLeft > 0) {
            delayLeft--;
        } else if (offLeft > 0) {
            offLeft--;
            if (antiKickMode.getValue() == AntiKickMode.Normal) {
                BlockPos pos = mc.player.getBlockPos().down();
                if (mc.world.getBlockState(pos).isAir() ||
                        (!mc.world.getBlockState(pos).isAir() && mc.player.getY() >= pos.getY() + 1.11)) {
                    mc.player.move(MovementType.SELF, new Vec3d(0, -0.1, 0));
                }
            }
        } else {
            delayLeft = delay.getIntValue();
            offLeft = offTime.getIntValue();
        }

        // Flight mode handling
        switch (mode.getValue()) {
            case Mode.Velocity -> {
                mc.player.getAbilities().flying = false;
                mc.player.setVelocity(0, 0, 0);
                Vec3d velocity = mc.player.getVelocity();
                double verticalSpeed = speed.getValue() * (verticalSpeedMatch.getValue() ? 10f : 5f);
                if (mc.options.jumpKey.isPressed()) {
                    mc.player.setVelocity(velocity.x, verticalSpeed, velocity.z);
                }
                if (mc.options.sneakKey.isPressed() && !noSneak.getValue()) {
                    mc.player.setVelocity(velocity.x, -verticalSpeed, velocity.z);
                }
                if (noSneak.getValue()) {
                    mc.player.setOnGround(true);
                }
                mc.player.move(MovementType.SELF, mc.player.getVelocity());
            }
            case Mode.Abilities -> {
                if (!mc.player.isSpectator()) {
                    mc.player.getAbilities().setFlySpeed(speed.getFloatValue());
                    mc.player.getAbilities().flying = true;
                    if (!mc.player.getAbilities().creativeMode) {
                        mc.player.getAbilities().allowFlying = true;
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode.getValue());
        }
    }

    @EventListener
    public void onPacketSend(PacketSendEvent event) {
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket packet)) return;

        // Improved NoFall handling based on Meteor
        if (noFall.getValue() && !packet.isOnGround() && !mc.player.isGliding()) {
            ((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
            return;
        }

        // Enhanced anti-kick packet handling from Meteor
        if (antiKickMode.getValue() != AntiKickMode.Packet) return;

        double currentY = packet.getY(Double.MAX_VALUE);
        if (currentY != Double.MAX_VALUE) {
            antiKickPacket(packet, currentY, event);
        } else {
            // Convert to full packet with Y position if needed
            PlayerMoveC2SPacket fullPacket;
            if (packet.changesLook()) {
                fullPacket = new PlayerMoveC2SPacket.Full(
                        mc.player.getX(),
                        mc.player.getY(),
                        mc.player.getZ(),
                        packet.getYaw(0),
                        packet.getPitch(0),
                        packet.isOnGround(),
                        packet.horizontalCollision()
                );
            } else {
                fullPacket = new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX(),
                        mc.player.getY(),
                        mc.player.getZ(),
                        packet.isOnGround(),
                        packet.horizontalCollision()
                );
            }
            event.setPacket(fullPacket);
            antiKickPacket(fullPacket, mc.player.getY(), event);
        }
    }

    private void antiKickPacket(PlayerMoveC2SPacket packet, double currentY, PacketSendEvent event) {
        if (delayLeft <= 0 && lastPacketY != Double.MAX_VALUE && shouldFlyDown(currentY, lastPacketY) && isEntityOnAir(mc.player)) {
            if (packet instanceof PlayerMoveC2SPacket.Full full) {
                full = new PlayerMoveC2SPacket.Full(
                        packet.getX(0),
                        lastPacketY - 0.03130,
                        packet.getZ(0),
                        packet.getYaw(0),
                        packet.getPitch(0),
                        full.isOnGround(),
                        full.horizontalCollision()
                );
                event.setPacket(full);
            } else if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround pos) {
                pos = new PlayerMoveC2SPacket.PositionAndOnGround(
                        packet.getX(0),
                        lastPacketY - 0.03130,
                        packet.getZ(0),
                        pos.isOnGround(),
                        packet.horizontalCollision()
                );
                event.setPacket(pos);
            }
        } else {
            lastPacketY = currentY;
        }
    }

    private boolean shouldFlyDown(double currentY, double lastY) {
        return currentY >= lastY || (lastY - currentY < 0.03130);
    }

    private void abilitiesOff() {
        mc.player.getAbilities().flying = false;
        mc.player.getAbilities().setFlySpeed(0.05f);
        if (!mc.player.getAbilities().creativeMode) {
            mc.player.getAbilities().allowFlying = false;
        }
    }

    private boolean isEntityOnAir(Entity entity) {
        return entity.getWorld().getStatesInBox(entity.getBoundingBox()
                        .expand(0.0625)
                        .stretch(0.0, -0.55, 0.0))
                .allMatch(state -> state.isAir());
    }

    public enum Mode {
        Abilities,
        Velocity
    }

    public enum AntiKickMode {
        Normal,
        Packet,
        None
    }
}