package com.h1ggsk.radon.module.modules.combat;

import com.h1ggsk.radon.mixin.PlayerInventoryAccessor;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.FakeInvScreen;
import com.h1ggsk.radon.utils.EncryptedString;

import java.util.function.Predicate;

public final class AutoInventoryTotem extends Module {
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0.0, 20.0, 0.0, 1.0);
    private final BooleanSetting hotbar = new BooleanSetting(EncryptedString.of("Hotbar"), true)
            .setDescription(EncryptedString.of("Puts keybindListener totem in your hotbar as well"));
    private final NumberSetting totemSlot = new NumberSetting(EncryptedString.of("Totem Slot"), 1.0, 9.0, 1.0, 1.0);
    private final BooleanSetting autoSwitch = new BooleanSetting(EncryptedString.of("Auto Switch"), false)
            .setDescription(EncryptedString.of("Switches to totem slot when in inventory"));
    private final BooleanSetting forceTotem = new BooleanSetting(EncryptedString.of("Force Totem"), false)
            .setDescription(EncryptedString.of("Replaces items in slot with totem"));
    private final BooleanSetting autoOpen = new BooleanSetting(EncryptedString.of("Auto Open"), false)
            .setDescription(EncryptedString.of("Automatically opens inventory"));
    private final NumberSetting stayOpenDuration = new NumberSetting(EncryptedString.of("Stay Open For"), 0.0, 20.0, 0.0, 1.0);

    private int delayCounter = -1;
    private int stayOpenCounter = -1;
    private boolean wasInInventory = false;

    public AutoInventoryTotem() {
        super(EncryptedString.of("Auto Inv Totem"), EncryptedString.of("Automatically equips totems"), -1, Category.COMBAT);
        this.addSettings(this.delay, this.hotbar, this.totemSlot, this.autoSwitch, this.forceTotem, this.autoOpen, this.stayOpenDuration);
    }

    @Override
    public void onEnable() {
        this.delayCounter = -1;
        this.stayOpenCounter = -1;
        this.wasInInventory = false;
        super.onEnable();
    }

    @EventListener
    public void onTick(final StartTickEvent event) {
        boolean isInInventory = mc.currentScreen instanceof InventoryScreen || mc.currentScreen instanceof FakeInvScreen;

        // Handle auto opening inventory
        if (this.shouldOpenInventory() && this.autoOpen.getValue() && !isInInventory) {
            mc.setScreen(new FakeInvScreen(mc.player));
            isInInventory = true;
        }

        // Reset counters when leaving inventory
        if (!isInInventory) {
            if (wasInInventory) {
                this.delayCounter = -1;
                this.stayOpenCounter = -1;
            }
            wasInInventory = false;
            return;
        }

        wasInInventory = true;

        // Initialize counters if needed
        if (this.delayCounter == -1) {
            this.delayCounter = this.delay.getIntValue();
        }
        if (this.stayOpenCounter == -1) {
            this.stayOpenCounter = this.stayOpenDuration.getIntValue();
        }

        // Handle delay
        if (this.delayCounter > 0) {
            this.delayCounter--;
            return;
        }

        PlayerInventory inventory = mc.player.getInventory();

        // Auto switch to totem slot if enabled
        if (this.autoSwitch.getValue()) {
            inventory.setSelectedSlot(this.totemSlot.getIntValue() - 1);
        }

        // Equip totem in offhand
        if (inventory.getStack(40).getItem() != Items.TOTEM_OF_UNDYING) {
            int totemSlot = this.findTotemSlot();
            if (totemSlot != -1) {
                mc.interactionManager.clickSlot(
                        ((InventoryScreen) mc.currentScreen).getScreenHandler().syncId,
                        totemSlot,
                        40, // Offhand slot
                        SlotActionType.SWAP,
                        mc.player
                );
                return;
            }
        }

        // Equip totem in hotbar if enabled
        if (this.hotbar.getValue()) {
            ItemStack mainHandStack = mc.player.getEquippedStack(EquipmentSlot.MAINHAND);
            boolean shouldReplace = mainHandStack.isEmpty() ||
                    (this.forceTotem.getValue() && mainHandStack.getItem() != Items.TOTEM_OF_UNDYING);

            if (shouldReplace) {
                int totemSlot = this.findTotemSlot();
                if (totemSlot != -1) {
                    mc.interactionManager.clickSlot(
                            ((InventoryScreen) mc.currentScreen).getScreenHandler().syncId,
                            totemSlot,
                            inventory.getSelectedSlot(),
                            SlotActionType.SWAP,
                            mc.player
                    );
                    return;
                }
            }
        }

        // Auto close inventory if enabled and totems are equipped
        if (this.isTotemEquipped() && this.autoOpen.getValue()) {
            if (this.stayOpenCounter > 0) {
                this.stayOpenCounter--;
            } else {
                mc.currentScreen.close();
                this.stayOpenCounter = this.stayOpenDuration.getIntValue();
            }
        }
    }

    private boolean isTotemEquipped() {
        boolean offhandHasTotem = mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING;

        if (this.hotbar.getValue()) {
            boolean hotbarHasTotem = mc.player.getInventory()
                    .getStack(this.totemSlot.getIntValue() - 1)
                    .getItem() == Items.TOTEM_OF_UNDYING;
            return offhandHasTotem && hotbarHasTotem;
        }

        return offhandHasTotem;
    }

    private boolean shouldOpenInventory() {
        boolean needsTotemInOffhand = mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING;
        boolean hasTotems = this.countTotems(item -> item == Items.TOTEM_OF_UNDYING) > 0;

        if (this.hotbar.getValue()) {
            boolean needsTotemInHotbar = mc.player.getInventory()
                    .getStack(this.totemSlot.getIntValue() - 1)
                    .getItem() != Items.TOTEM_OF_UNDYING;
            return (needsTotemInOffhand || needsTotemInHotbar) && hasTotems;
        }

        return needsTotemInOffhand && hasTotems;
    }

    private int findTotemSlot() {
        PlayerInventoryAccessor inventory = ((PlayerInventoryAccessor) mc.player.getInventory());
        for (int i = 0; i < inventory.getMain().size(); i++) {
            if (inventory.getMain().get(i).getItem() == Items.TOTEM_OF_UNDYING) {
                return i < 9 ? i + 36 : i; // Convert to container slot IDs
            }
        }
        return -1;
    }

    private int countTotems(Predicate<Item> predicate) {
        int count = 0;
        PlayerInventoryAccessor inventory = ((PlayerInventoryAccessor) mc.player.getInventory());
        for (ItemStack stack : inventory.getMain()) {
            if (predicate.test(stack.getItem())) {
                count += stack.getCount();
            }
        }
        return count;
    }
}