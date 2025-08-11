package com.h1ggsk.radon.module.modules.combat;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.mixin.HandledScreenMixin;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public final class HoverTotem extends Module {
    private final NumberSetting tickDelay = new NumberSetting(EncryptedString.of("Tick Delay"), 0.0, 20.0, 0.0, 1.0).getValue(EncryptedString.of("Ticks to wait between operations"));
    private final BooleanSetting hotbarTotem = new BooleanSetting(EncryptedString.of("Hotbar Totem"), true).setDescription(EncryptedString.of("Also places keybindListener totem in your preferred hotbar slot"));
    private final NumberSetting hotbarSlot = new NumberSetting(EncryptedString.of("Hotbar Slot"), 1.0, 9.0, 1.0, 1.0).getValue(EncryptedString.of("Your preferred hotbar slot for totem (1-9)"));
    private final BooleanSetting autoSwitchToTotem = new BooleanSetting(EncryptedString.of("Auto Switch To Totem"), false).setDescription(EncryptedString.of("Automatically switches to totem slot when inventory is opened"));
    private int remainingDelay;

    public HoverTotem() {
        super(EncryptedString.of("Hover Totem"), EncryptedString.of("Equips keybindListener totem in offhand and optionally hotbar when hovering over one in inventory"), -1, Category.COMBAT);
        this.addSettings(this.tickDelay, this.hotbarTotem, this.hotbarSlot, this.autoSwitchToTotem);
    }

    @Override
    public void onEnable() {
        this.resetDelay();
        super.onEnable();
    }

    @EventListener
    public void onTick(final StartTickEvent event) {
        if (mc.player == null) {
            return;
        }
        final Screen currentScreen = mc.currentScreen;
        if (!(mc.currentScreen instanceof InventoryScreen)) {
            this.resetDelay();
            return;
        }
        final Slot focusedSlot = ((HandledScreenMixin) currentScreen).getFocusedSlot();
        if (focusedSlot == null || focusedSlot.getIndex() > 35) {
            return;
        }
        if (this.autoSwitchToTotem.getValue()) {
            mc.player.getInventory().setSelectedSlot(this.hotbarSlot.getIntValue() - 1);
        }
        if (focusedSlot.getStack().getItem() != Items.TOTEM_OF_UNDYING) {
            return;
        }
        if (this.remainingDelay > 0) {
            --this.remainingDelay;
            return;
        }
        final int index = focusedSlot.getIndex();
        final int syncId = ((InventoryScreen) currentScreen).getScreenHandler().syncId;
        if (!mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            this.equipOffhandTotem(syncId, index);
            return;
        }
        if (this.hotbarTotem.getValue()) {
            final int n = this.hotbarSlot.getIntValue() - 1;
            if (!mc.player.getInventory().getStack(n).isOf(Items.TOTEM_OF_UNDYING)) {
                this.equipHotbarTotem(syncId, index, n);
            }
        }
    }

    private void equipOffhandTotem(final int n, final int n2) {
        mc.interactionManager.clickSlot(n, n2, 40, SlotActionType.SWAP, mc.player);
        this.resetDelay();
    }

    private void equipHotbarTotem(final int n, final int n2, final int n3) {
        mc.interactionManager.clickSlot(n, n2, n3, SlotActionType.SWAP, mc.player);
        this.resetDelay();
    }

    private void resetDelay() {
        this.remainingDelay = this.tickDelay.getIntValue();
    }
}
