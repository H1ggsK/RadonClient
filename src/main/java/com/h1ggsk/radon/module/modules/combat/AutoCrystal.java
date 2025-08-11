package com.h1ggsk.radon.module.modules.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.PreItemUseEvent;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BindSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.BlockUtil;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.KeyUtils;

public final class AutoCrystal extends Module {
    private final BindSetting activateKey = new BindSetting(EncryptedString.of("Activate Key"), 1, false).setDescription(EncryptedString.of("Key that does the crystalling"));
    private final NumberSetting placeDelay = new NumberSetting(EncryptedString.of("Place Delay"), 0.0, 20.0, 0.0, 1.0);
    private final NumberSetting breakDelay = new NumberSetting(EncryptedString.of("Break Delay"), 0.0, 20.0, 0.0, 1.0);
    private final NumberSetting crystalSpeed = new NumberSetting(EncryptedString.of("Crystal Speed"), 1.0, 5.0, 1.0, 0.1);

    private int placeDelayCounter;
    private int breakDelayCounter;
    public boolean isActive;

    public AutoCrystal() {
        super(EncryptedString.of("Auto Crystal"), EncryptedString.of("Automatically crystals fast for you"), -1, Category.COMBAT);
        this.addSettings(this.activateKey, this.placeDelay, this.breakDelay, this.crystalSpeed);
    }

    @Override
    public void onEnable() {
        this.resetCounters();
        this.isActive = false;
        super.onEnable();
    }

    private void resetCounters() {
        this.placeDelayCounter = 0;
        this.breakDelayCounter = 0;
    }

    @EventListener
    public void onTick(final StartTickEvent startTickEvent) {
        if (mc.currentScreen != null) {
            return;
        }
        this.updateCounters();
        if (mc.player.isUsingItem()) {
            return;
        }
        if (!this.isKeyActive()) {
            return;
        }
        if (mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL) {
            return;
        }
        this.handleInteraction();
    }

    private void updateCounters() {
        if (this.placeDelayCounter > 0) {
            --this.placeDelayCounter;
        }
        if (this.breakDelayCounter > 0) {
            --this.breakDelayCounter;
        }
    }

    private boolean isKeyActive() {
        final int d = this.activateKey.getValue();
        if (d != -1 && !KeyUtils.isKeyPressed(d)) {
            this.resetCounters();
            return this.isActive = false;
        }
        return this.isActive = true;
    }

    private void handleInteraction() {
        final HitResult crosshairTarget = mc.crosshairTarget;
        if (mc.crosshairTarget instanceof BlockHitResult) {
            this.handleBlockInteraction((BlockHitResult) crosshairTarget);
        } else if (mc.crosshairTarget instanceof final EntityHitResult entityHitResult) {
            this.handleEntityInteraction(entityHitResult);
        }
    }

    private void handleBlockInteraction(final BlockHitResult blockHitResult) {
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        if (this.placeDelayCounter > 0) {
            return;
        }
        final BlockPos blockPos = blockHitResult.getBlockPos();
        if ((BlockUtil.isBlockAtPosition(blockPos, Blocks.OBSIDIAN) || BlockUtil.isBlockAtPosition(blockPos, Blocks.BEDROCK)) && this.isValidCrystalPlacement(blockPos)) {
            BlockUtil.interactWithBlock(blockHitResult, true);
            this.placeDelayCounter = (int) (this.placeDelay.getValue() / this.crystalSpeed.getValue());
        }
    }

    private void handleEntityInteraction(final EntityHitResult entityHitResult) {
        if (this.breakDelayCounter > 0) {
            return;
        }
        final Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof EndCrystalEntity) && !(entity instanceof SlimeEntity)) {
            return;
        }
        mc.interactionManager.attackEntity(mc.player, entity);
        mc.player.swingHand(Hand.MAIN_HAND);
        this.breakDelayCounter = (int) (this.breakDelay.getValue() / this.crystalSpeed.getValue());
    }

    @EventListener
    public void onPreItemUse(final PreItemUseEvent preItemUseEvent) {
        if (mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL) {
            return;
        }
        if (!(mc.crosshairTarget instanceof BlockHitResult blockHitResult)) {
            return;
        }
        if (mc.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            return;
        }
        final BlockPos blockPos = blockHitResult.getBlockPos();
        if (BlockUtil.isBlockAtPosition(blockPos, Blocks.OBSIDIAN) || BlockUtil.isBlockAtPosition(blockPos, Blocks.BEDROCK)) {
            preItemUseEvent.cancel();
        }
    }

    private boolean isValidCrystalPlacement(final BlockPos blockPos) {
        final BlockPos up = blockPos.up();
        if (!mc.world.isAir(up)) {
            return false;
        }
        final int getX = up.getX();
        final int getY = up.getY();
        final int compareTo = up.getZ();
        return mc.world.getOtherEntities(null, new Box(getX, getY, compareTo, getX + 1.0, getY + 2.0, compareTo + 1.0)).isEmpty();
    }
}
