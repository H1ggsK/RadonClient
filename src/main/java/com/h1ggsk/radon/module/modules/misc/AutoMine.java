package com.h1ggsk.radon.module.modules.misc;

import com.h1ggsk.radon.Radon;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public final class AutoMine extends Module {
    private final BooleanSetting lockView = new BooleanSetting(EncryptedString.of("Lock View"), true);
    private final NumberSetting pitch = new NumberSetting(EncryptedString.of("Pitch"), -180.0, 180.0, 0.0, 0.1);
    private final NumberSetting yaw = new NumberSetting(EncryptedString.of("Yaw"), -180.0, 180.0, 0.0, 0.1);

    public AutoMine() {
        super(EncryptedString.of("Auto Mine"), EncryptedString.of("Module that allows players to automatically mine"), -1, Category.MISC);
        this.addSettings(this.lockView, this.pitch, this.yaw);
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
    public void onTick(final StartTickEvent event) {
        if (mc.currentScreen != null) {
            return;
        }
        final Module moduleByClass = Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(AutoEat.class);
        if (moduleByClass.isEnabled() && ((AutoEat) moduleByClass).shouldEat()) {
            return;
        }
        this.processMiningAction(true);
        if (this.lockView.getValue()) {
            final float getYaw = mc.player.getYaw();
            final float getPitch = mc.player.getPitch();
            final float g = this.yaw.getFloatValue();
            final float g2 = this.pitch.getFloatValue();
            if (getYaw != g || getPitch != g2) {
                mc.player.setYaw(g);
                mc.player.setPitch(g2);
            }
        }
    }

    private void processMiningAction(final boolean b) {
        if (!mc.player.isUsingItem()) {
            if (b && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                final BlockHitResult blockHitResult = (BlockHitResult) mc.crosshairTarget;
                final BlockPos blockPos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
                if (!mc.world.getBlockState(blockPos).isAir()) {
                    final Direction side = blockHitResult.getSide();
                    if (mc.interactionManager.updateBlockBreakingProgress(blockPos, side)) {
                        mc.particleManager.addBlockBreakingParticles(blockPos, side);
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                }
            } else {
                mc.interactionManager.cancelBlockBreaking();
            }
        }
    }
}
