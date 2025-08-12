package com.h1ggsk.radon.module.modules.combat;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BindSetting;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.InventoryUtil;
import com.h1ggsk.radon.utils.KeyUtils;

import java.util.function.Predicate;

public final class ElytraSwap extends Module {
    private final BindSetting activateKey = new BindSetting(EncryptedString.of("Activate Key"), 71, false);
    private final NumberSetting swapDelay = new NumberSetting(EncryptedString.of("Delay"), 0.0, 20.0, 0.0, 1.0);
    private final BooleanSetting switchBack = new BooleanSetting(EncryptedString.of("Switch Back"), true);
    private final NumberSetting switchDelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0.0, 20.0, 0.0, 1.0);
    private final BooleanSetting moveToSlot = new BooleanSetting(EncryptedString.of("Move to slot"), true).setDescription("If elytra is not in hotbar it will move it from inventory to preferred slot");
    private final NumberSetting elytraSlot = new NumberSetting(EncryptedString.of("Elytra Slot"), 1.0, 9.0, 9.0, 1.0).getValue(EncryptedString.of("Your preferred elytra slot"));
    private boolean isSwapping;
    private boolean isSwinging;
    private boolean isItemSwapped;
    private int swapCounter;
    private int switchCounter;
    private int activationCooldown;
    private int originalSlot;

    public ElytraSwap() {
        super(EncryptedString.of("Elytra Swap"), EncryptedString.of("Seamlessly swap between an Elytra and keybindListener Chestplate with keybindListener configurable keybinding"), -1, Category.COMBAT);
        this.addSettings(this.activateKey, this.swapDelay, this.switchBack, this.switchDelay, this.moveToSlot, this.elytraSlot);
    }

    @Override
    public void onEnable() {
        this.resetState();
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
        if (mc.player == null) {
            return;
        }
        if (this.activationCooldown > 0) {
            --this.activationCooldown;
        } else if (KeyUtils.isKeyPressed(this.activateKey.getValue())) {
            this.isSwapping = true;
            this.activationCooldown = 4;
        }
        if (this.isSwapping) {
            if (this.originalSlot == -1) {
                this.originalSlot = mc.player.getInventory().getSelectedSlot();
            }
            if (this.swapCounter < this.swapDelay.getIntValue()) {
                ++this.swapCounter;
                return;
            }
            Predicate<Item> predicate;

            if (mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
                predicate = (item -> item.getDefaultStack().isIn(ConventionalItemTags.ARMORS) && item.getDefaultStack().isIn(ItemTags.CHEST_ARMOR));
            } else {
                predicate = (item2 -> item2.equals(Items.ELYTRA));
            }

            if (!this.isItemSwapped) {
                if (!InventoryUtil.swapItem(predicate)) {
                    if (!this.moveToSlot.getValue()) {
                        this.resetState();
                        return;
                    }

                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 9, this.elytraSlot.getIntValue() - 1, SlotActionType.SWAP, mc.player);
                    this.swapCounter = 0;
                    return;
                } else {
                    this.isItemSwapped = true;
                }
            }
            if (!this.isSwinging) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                this.isSwinging = true;
            }
            if (this.switchBack.getValue()) {
                this.handleSwitchBack();
            } else {
                this.resetState();
            }
        }
    }

    private void handleSwitchBack() {
        if (this.switchCounter < this.switchDelay.getIntValue()) {
            ++this.switchCounter;
            return;
        }
        InventoryUtil.swap(this.originalSlot);
        this.resetState();
    }

    private void resetState() {
        this.originalSlot = -1;
        this.switchCounter = 0;
        this.swapCounter = 0;
        this.isSwapping = false;
        this.isSwinging = false;
        this.isItemSwapped = false;
    }
}
