package com.h1ggsk.radon.module.modules.misc;

import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
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

public final class KeyPearl extends Module {
    private final BindSetting activateKey = new BindSetting(EncryptedString.of("Activate Key"), -1, false);
    private final NumberSetting throwDelay = new NumberSetting(EncryptedString.of("Delay"), 0.0, 20.0, 0.0, 1.0);
    private final BooleanSetting switchBack = new BooleanSetting(EncryptedString.of("Switch Back"), true);
    private final NumberSetting switchBackDelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0.0, 20.0, 0.0, 1.0).getValue(EncryptedString.of("Delay after throwing pearl before switching back"));
    private boolean isActivated;
    private boolean hasThrown;
    private int currentThrowDelay;
    private int previousSlot;
    private int currentSwitchBackDelay;

    public KeyPearl() {
        super(EncryptedString.of("Key Pearl"), EncryptedString.of("Switches to an ender pearl and throws it when you press keybindListener bind"), -1, Category.MISC);
        this.addSettings(this.activateKey, this.throwDelay, this.switchBack, this.switchBackDelay);
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
        if (KeyUtils.isKeyPressed(this.activateKey.getValue())) {
            this.isActivated = true;
        }
        if (this.isActivated) {
            if (this.previousSlot == -1) {
                this.previousSlot = mc.player.getInventory().getSelectedSlot();
            }
            InventoryUtil.swap(Items.ENDER_PEARL);
            if (this.currentThrowDelay < this.throwDelay.getIntValue()) {
                ++this.currentThrowDelay;
                return;
            }
            if (!this.hasThrown) {
                final ActionResult interactItem = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                if (interactItem.isAccepted()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
                this.hasThrown = true;
            }
            if (this.switchBack.getValue()) {
                this.handleSwitchBack();
            } else {
                this.resetState();
            }
        }
    }

    private void handleSwitchBack() {
        if (this.currentSwitchBackDelay < this.switchBackDelay.getIntValue()) {
            ++this.currentSwitchBackDelay;
            return;
        }
        InventoryUtil.swap(this.previousSlot);
        this.resetState();
    }

    private void resetState() {
        this.previousSlot = -1;
        this.currentThrowDelay = 0;
        this.currentSwitchBackDelay = 0;
        this.isActivated = false;
        this.hasThrown = false;
    }
}
