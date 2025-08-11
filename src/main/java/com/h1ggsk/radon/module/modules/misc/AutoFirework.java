package com.h1ggsk.radon.module.modules.misc;

import com.h1ggsk.radon.Radon;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.PostItemUseEvent;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BindSetting;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.InventoryUtil;
import com.h1ggsk.radon.utils.KeyUtils;

public final class AutoFirework extends Module {
    private final BindSetting activateKey = new BindSetting(EncryptedString.of("Activate Key"), -1, false);
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0.0, 20.0, 0.0, 1.0);
    private final BooleanSetting switchBack = new BooleanSetting(EncryptedString.of("Switch Back"), true);
    private final NumberSetting switchDelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0.0, 20.0, 0.0, 1.0).getValue(EncryptedString.of("Delay after using firework before switching back."));
    private boolean isFireworkActive;
    private boolean hasUsedFirework;
    private int useDelayCounter;
    private int previousSelectedSlot;
    private int switchDelayCounter;
    private int cooldownCounter;

    public AutoFirework() {
        super(EncryptedString.of("Auto Firework"), EncryptedString.of("Switches to keybindListener firework and uses it when you press keybindListener bind."), -1, Category.MISC);
        this.addSettings(this.activateKey, this.delay, this.switchBack, this.switchDelay);
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
        if (this.cooldownCounter > 0) {
            --this.cooldownCounter;
            return;
        }
        if (mc.player != null && KeyUtils.isKeyPressed(this.activateKey.getValue()) && (Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(ElytraGlide.class).isEnabled() || mc.player.isGliding()) && mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) && !mc.player.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.FIREWORK_ROCKET) && !mc.player.getMainHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD) && !(mc.player.getMainHandStack().isIn(ConventionalItemTags.ARMORS))) {
            this.isFireworkActive = true;
        }
        if (this.isFireworkActive) {
            if (this.previousSelectedSlot == -1) {
                this.previousSelectedSlot = mc.player.getInventory().getSelectedSlot();
            }
            if (!InventoryUtil.swap(Items.FIREWORK_ROCKET)) {
                this.resetState();
                return;
            }
            if (this.useDelayCounter < this.delay.getIntValue()) {
                ++this.useDelayCounter;
                return;
            }
            if (!this.hasUsedFirework) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                this.hasUsedFirework = true;
            }
            if (this.switchBack.getValue()) {
                this.handleSwitchBack();
            } else {
                this.resetState();
            }
        }
    }

    private void handleSwitchBack() {
        if (this.switchDelayCounter < this.switchDelay.getIntValue()) {
            ++this.switchDelayCounter;
            return;
        }
        InventoryUtil.swap(this.previousSelectedSlot);
        this.resetState();
    }

    private void resetState() {
        this.previousSelectedSlot = -1;
        this.useDelayCounter = 0;
        this.switchDelayCounter = 0;
        this.cooldownCounter = 4;
        this.isFireworkActive = false;
        this.hasUsedFirework = false;
    }

    @EventListener
    public void onPostItemUse(final PostItemUseEvent postItemUseEvent) {
        if (mc.player.getMainHandStack().isOf(Items.FIREWORK_ROCKET)) {
            this.hasUsedFirework = true;
        }
        if (this.cooldownCounter > 0) {
            postItemUseEvent.cancel();
        }
    }
}
