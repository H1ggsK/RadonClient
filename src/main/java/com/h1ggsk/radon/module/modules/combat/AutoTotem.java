package com.h1ggsk.radon.module.modules.combat;

import com.h1ggsk.radon.Radon;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.modules.donut.RtpBaseFinder;
import com.h1ggsk.radon.module.modules.donut.TunnelBaseFinder;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public final class AutoTotem extends Module {
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0.0, 5.0, 1.0, 1.0);
    private int delayCounter;

    public AutoTotem() {
        super(EncryptedString.of("Auto Totem"), EncryptedString.of("Automatically holds totem in your off hand"), -1, Category.COMBAT);
        this.addSettings(this.delay);
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
        if (mc.player == null) {
            return;
        }
        final Module rtpBaseFinder = Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(RtpBaseFinder.class);
        if (rtpBaseFinder.isEnabled() && ((RtpBaseFinder) rtpBaseFinder).isRepairingActive()) {
            return;
        }
        final Module tunnelBaseFinder = Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(TunnelBaseFinder.class);
        if (tunnelBaseFinder.isEnabled() && ((TunnelBaseFinder) tunnelBaseFinder).isDigging()) {
            return;
        }
        if (mc.player.getInventory().getStack(40).getItem() == Items.TOTEM_OF_UNDYING) {
            this.delayCounter = this.delay.getIntValue();
            return;
        }
        if (this.delayCounter > 0) {
            --this.delayCounter;
            return;
        }
        final int slot = this.findItemSlot(Items.TOTEM_OF_UNDYING);
        if (slot == -1) {
            return;
        }
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, convertSlotIndex(slot), 40, SlotActionType.SWAP, mc.player);
        this.delayCounter = this.delay.getIntValue();
    }

    public int findItemSlot(final Item item) {
        if (mc.player == null) {
            return -1;
        }
        for (int i = 0; i < 36; ++i) {
            if (mc.player.getInventory().getStack(i).isOf(item)) {
                return i;
            }
        }
        return -1;
    }

    private static int convertSlotIndex(final int slotIndex) {
        if (slotIndex < 9) {
            return 36 + slotIndex;
        }
        return slotIndex;
    }
}