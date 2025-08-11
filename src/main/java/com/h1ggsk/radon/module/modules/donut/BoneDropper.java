package com.h1ggsk.radon.module.modules.donut;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.ModeSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public final class BoneDropper extends Module {
    private final ModeSetting<Mode> dropMode = new ModeSetting<>(EncryptedString.of("Mode"), Mode.SPAWNER, Mode.class);
    private final NumberSetting dropDelay = new NumberSetting(EncryptedString.of("Drop Delay"), 0.0, 120.0, 30.0, 1.0).getValue(EncryptedString.of("How often it should start dropping bones in minutes"));
    private final NumberSetting pageSwitchDelay = new NumberSetting(EncryptedString.of("Page Switch Delay"), 0.0, 720.0, 4.0, 1.0).getValue(EncryptedString.of("How often it should switch pages in seconds"));
    private int delayCounter;
    private boolean isPageSwitching;

    public BoneDropper() {
        super(EncryptedString.of("Bone Dropper"), EncryptedString.of("Automatically drops bones from spawner"), -1, Category.DONUT);
        this.addSettings(this.dropMode, this.dropDelay, this.pageSwitchDelay);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.delayCounter = 20;
        this.isPageSwitching = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventListener
    public void onTick(final StartTickEvent event) {
        if (this.delayCounter > 0) {
            --this.delayCounter;
            return;
        }
        if (mc.player == null) {
            return;
        }
        if (this.dropMode.isMode(Mode.SPAWNER)) {
            if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                KeyBinding.onKeyPressed(InputUtil.Type.MOUSE.createFromCode(1));
                this.delayCounter = 20;
                return;
            }
            if (mc.player.currentScreenHandler.getSlot(13).getStack().isOf(Items.SKELETON_SKULL)) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 11, 0, SlotActionType.PICKUP, mc.player);
                this.delayCounter = 20;
                return;
            }
            if (!mc.player.currentScreenHandler.getSlot(53).getStack().isOf(Items.GOLD_INGOT)) {
                mc.player.closeHandledScreen();
                this.delayCounter = 20;
                return;
            }
            if (mc.player.currentScreenHandler.getSlot(48).getStack().isOf(Items.ARROW)) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 48, 0, SlotActionType.PICKUP, mc.player);
                this.delayCounter = 20;
                return;
            }
            boolean b = true;
            for (int i = 0; i < 45; ++i) {
                if (!mc.player.currentScreenHandler.getSlot(i).getStack().isOf(Items.BONE)) {
                    b = false;
                    break;
                }
            }
            if (b) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 52, 1, SlotActionType.THROW, mc.player);
                this.isPageSwitching = true;
                this.delayCounter = this.pageSwitchDelay.getIntValue() * 20;
            } else if (this.isPageSwitching) {
                this.isPageSwitching = false;
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 50, 0, SlotActionType.PICKUP, mc.player);
                this.delayCounter = 20;
            } else {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 1, SlotActionType.THROW, mc.player);
                this.isPageSwitching = false;
                this.delayCounter = 1200 * this.dropDelay.getIntValue();
            }
        } else {
            final ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
            if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                mc.getNetworkHandler().sendChatCommand("order");
                this.delayCounter = 20;
                return;
            }
            if (((GenericContainerScreenHandler) currentScreenHandler).getRows() == 6) {
                if (currentScreenHandler.getSlot(49).getStack().isOf(Items.MAP)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 51, 0, SlotActionType.PICKUP, mc.player);
                    this.delayCounter = 20;
                    return;
                }
                for (int j = 0; j < 45; ++j) {
                    if (currentScreenHandler.getSlot(j).getStack().isOf(Items.BONE)) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, j, 1, SlotActionType.THROW, mc.player);
                        this.delayCounter = this.dropDelay.getIntValue();
                        return;
                    }
                }
                int n;
                if (this.isPageSwitching) {
                    n = 45;
                } else {
                    n = 53;
                }
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, n, 0, SlotActionType.PICKUP, mc.player);
                this.isPageSwitching = !this.isPageSwitching;
                this.delayCounter = this.pageSwitchDelay.getIntValue();
            } else if (((GenericContainerScreenHandler) currentScreenHandler).getRows() == 3) {
                if (mc.currentScreen == null) {
                    return;
                }
                if (mc.currentScreen.getTitle().getString().contains("Your Orders")) {
                    for (int k = 0; k < 26; ++k) {
                        if (currentScreenHandler.getSlot(k).getStack().isOf(Items.BONE)) {
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, k, 0, SlotActionType.PICKUP, mc.player);
                            this.delayCounter = 20;
                            return;
                        }
                    }
                    this.delayCounter = 200;
                    return;
                }
                if (mc.currentScreen.getTitle().getString().contains("Edit Order")) {
                    if (currentScreenHandler.getSlot(13).getStack().isOf(Items.CHEST)) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 13, 0, SlotActionType.PICKUP, mc.player);
                        this.delayCounter = 20;
                        return;
                    }
                    if (currentScreenHandler.getSlot(15).getStack().isOf(Items.CHEST)) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 15, 0, SlotActionType.PICKUP, mc.player);
                        this.delayCounter = 20;
                        return;
                    }
                }
                this.delayCounter = 200;
            }
        }
    }

     enum Mode {
        SPAWNER("Spawner", 0),
        ORDER("Order", 1);

        Mode(final String name, final int ordinal) {
        }
    }

}
