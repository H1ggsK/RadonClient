package com.h1ggsk.radon.module.modules.misc;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.PreItemUseEvent;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.InventoryUtil;

public final class ElytraGlide extends Module {
    private boolean isFlyingTriggered;
    private boolean isFireworkUsed;
    private boolean isJumpKeyPressed;

    public ElytraGlide() {
        super(EncryptedString.of("Elytra Glide"), EncryptedString.of("Starts flying when attempting to use keybindListener firework"), -1, Category.MISC);
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
        if (this.isJumpKeyPressed) {
            this.isJumpKeyPressed = false;
            KeyBinding.setKeyPressed(mc.options.jumpKey.getDefaultKey(), false);
            return;
        }
        if (this.isFlyingTriggered) {
            if (this.isFireworkUsed) {
                final int selectedSlot = mc.player.getInventory().getSelectedSlot();
                if (!mc.player.getMainHandStack().isOf(Items.FIREWORK_ROCKET) && !InventoryUtil.swap(Items.FIREWORK_ROCKET)) {
                    return;
                }
                mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());
                mc.player.getInventory().setSelectedSlot(selectedSlot);
                this.isFireworkUsed = false;
                this.isFlyingTriggered = false;
            } else if (mc.player.isOnGround()) {
                KeyBinding.setKeyPressed(mc.options.jumpKey.getDefaultKey(), true);
                this.isJumpKeyPressed = true;
            } else {
                KeyBinding.setKeyPressed(mc.options.jumpKey.getDefaultKey(), true);
                this.isJumpKeyPressed = true;
                this.isFireworkUsed = true;
            }
        }
    }

    @EventListener
    public void onPreItemUse(final PreItemUseEvent event) {
        if (!mc.player.getMainHandStack().isOf(Items.FIREWORK_ROCKET) || !mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) || !mc.player.isGliding()) return;
        this.isFlyingTriggered = true;
    }
}