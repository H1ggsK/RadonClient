package com.h1ggsk.radon.module.modules.donut;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.InventoryUtil;

public final class AutoSpawnerSell extends Module {
    private final NumberSetting dropDelay = new NumberSetting(EncryptedString.of("Drop Delay"), 0.0, 120.0, 30.0, 1.0).getValue(EncryptedString.of("How often it should start dropping bones in minutes"));
    private final NumberSetting pageAmount = new NumberSetting(EncryptedString.of("Page Amount"), 1.0, 10.0, 2.0, 1.0).getValue(EncryptedString.of("How many pages should it drop before selling"));
    private final NumberSetting pageSwitchDelay = new NumberSetting(EncryptedString.of("Page Switch Delay"), 0.0, 720.0, 4.0, 1.0).getValue(EncryptedString.of("How often it should switch pages in seconds"));
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("delay"), 0.0, 20.0, 1.0, 1.0).getValue(EncryptedString.of("What should be delay in ticks"));
    private int delayCounter;
    private int pageCounter;
    private boolean isProcessing;
    private boolean isSelling;
    private boolean isPageSwitching;

    public AutoSpawnerSell() {
        super(EncryptedString.of("Auto Spawner Sell"), EncryptedString.of("Automatically drops bones from spawner and sells them"), -1, Category.DONUT);
        this.addSettings(this.dropDelay, this.pageAmount, this.pageSwitchDelay, this.delay);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.delayCounter = 20;
        this.isProcessing = false;
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
        if (this.pageCounter >= this.pageAmount.getIntValue()) {
            this.isSelling = true;
            this.pageCounter = 0;
            this.delayCounter = 40;
            return;
        }
        if (this.isSelling) {
            final ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
            if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                mc.getNetworkHandler().sendChatCommand("order " + this.getOrderCommand());
                this.delayCounter = 20;
                return;
            }
            if (((GenericContainerScreenHandler) currentScreenHandler).getRows() == 6) {
                final ItemStack stack = currentScreenHandler.getSlot(47).getStack();
                if (stack.isOf(Items.AIR)) {
                    this.delayCounter = 2;
                    mc.player.closeHandledScreen();
                    return;
                }
                for (final Object next : stack.getTooltip(Item.TooltipContext.create(mc.world), mc.player, TooltipType.BASIC)) {
                    final String string = next.toString();
                    if (string.contains("Most Money Per Item") && (((Text) next).getStyle().toString().contains("white") || string.contains("white"))) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 47, 1, SlotActionType.QUICK_MOVE, mc.player);
                        this.delayCounter = 5;
                        return;
                    }
                }
                for (int i = 0; i < 44; ++i) {
                    if (currentScreenHandler.getSlot(i).getStack().isOf(this.getInventoryItem())) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.QUICK_MOVE, mc.player);
                        this.delayCounter = 10;
                        return;
                    }
                }
                this.delayCounter = 40;
                mc.player.closeHandledScreen();
            } else if (((GenericContainerScreenHandler) currentScreenHandler).getRows() == 4) {
                final int b = InventoryUtil.getSlot(Items.AIR);
                if (b <= 0) {
                    mc.player.closeHandledScreen();
                    this.delayCounter = 10;
                    return;
                }
                if (this.isPageSwitching && b == 36) {
                    this.isPageSwitching = false;
                    mc.player.closeHandledScreen();
                    return;
                }
                final Item j = this.getInventoryItem();
                while (true) {
                    final int n = 36;
                    final Item item = mc.player.currentScreenHandler.getStacks().get(n).getItem();
                    if (item != Items.AIR && item == j) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, n, 1, SlotActionType.QUICK_MOVE, mc.player);
                        this.delayCounter = this.delay.getIntValue();
                        if (this.delay.getIntValue() != 0) {
                            break;
                        }
                        continue;
                    }
                }
            } else if (((GenericContainerScreenHandler) currentScreenHandler).getRows() == 3) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 15, 1, SlotActionType.QUICK_MOVE, mc.player);
                this.isPageSwitching = true;
                this.delayCounter = 10;
            }
        } else {
            final ScreenHandler fishHook = mc.player.currentScreenHandler;
            if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                KeyBinding.onKeyPressed(InputUtil.Type.MOUSE.createFromCode(1));
                this.delayCounter = 20;
                return;
            }
            if (fishHook.getSlot(15).getStack().isOf(Items.LIME_STAINED_GLASS_PANE)) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 15, 1, SlotActionType.QUICK_MOVE, mc.player);
                this.delayCounter = 10;
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
            boolean b2 = true;
            for (int k = 0; k < 45; ++k) {
                if (!mc.player.currentScreenHandler.getSlot(k).getStack().isOf(Items.BONE)) {
                    b2 = false;
                    break;
                }
            }
            if (b2) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 52, 1, SlotActionType.THROW, mc.player);
                this.isProcessing = true;
                this.delayCounter = this.pageSwitchDelay.getIntValue() * 20;
                ++this.pageCounter;
            } else if (this.isProcessing) {
                this.isProcessing = false;
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 50, 0, SlotActionType.PICKUP, mc.player);
                this.delayCounter = 20;
            } else {
                this.isProcessing = false;
                if (this.pageCounter != 0) {
                    this.pageCounter = 0;
                    this.isSelling = true;
                    this.delayCounter = 40;
                    return;
                }
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 1, SlotActionType.THROW, mc.player);
                this.delayCounter = 1200 * this.dropDelay.getIntValue();
            }
        }
    }

    private Item getInventoryItem() {
        for (int i = 0; i < 35; ++i) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isOf(Items.AIR)) {
                return stack.getItem();
            }
        }
        return Items.AIR;
    }

    private String getOrderCommand() {
        final Item j = this.getInventoryItem();
        if (j.equals(Items.BONE)) {
            return "Bones";
        }
        return j.getName().getString();
    }
}
