package com.h1ggsk.radon.module.modules.donut;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.ModeSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.InventoryUtil;

public final class AutoSell extends Module {
    private final ModeSetting<Mode> mode = new ModeSetting<>(EncryptedString.of("Mode"), Mode.SELL, Mode.class);
    private final ModeSetting<Items> item = new ModeSetting<>(EncryptedString.of("Item"), Items.SEAPICKLE, Items.class);
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("delay"), 0.0, 20.0, 2.0, 1.0).getValue(EncryptedString.of("What should be delay in ticks"));
    private int delayCounter;
    private boolean isSlotProcessed;

    public AutoSell() {
        super(EncryptedString.of("Auto Sell"), EncryptedString.of("Automatically sells pickles"), -1, Category.DONUT);
        this.addSettings(this.mode, this.item, this.delay);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.delayCounter = 20;
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
        if (this.delayCounter > 0) {
            --this.delayCounter;
            return;
        }
        if (this.mode.getValue().equals(Mode.SELL)) {
            final ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
            if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) || ((GenericContainerScreenHandler) currentScreenHandler).getRows() != 5) {
                mc.getNetworkHandler().sendChatCommand("sell");
                this.delayCounter = 20;
                return;
            }
            if (InventoryUtil.getSlot(net.minecraft.item.Items.AIR) > 0) {
                int n;
                Item item;
                do {
                    n = 45;
                    item = mc.player.currentScreenHandler.getStacks().get(n).getItem();
                } while (item == net.minecraft.item.Items.AIR || (!this.item.getValue().equals(Items.ALL) && !item.equals(this.getSelectedItem())));
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, n, 1, SlotActionType.QUICK_MOVE, mc.player);
                this.delayCounter = this.delay.getIntValue();
                return;
            }
            mc.player.closeHandledScreen();
        } else {
            final ScreenHandler fishHook = mc.player.currentScreenHandler;
            if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                mc.getNetworkHandler().sendChatCommand("order " + this.getOrderCommand());
                this.delayCounter = 20;
                return;
            }
            if (((GenericContainerScreenHandler) fishHook).getRows() == 6) {
                final ItemStack stack = fishHook.getSlot(47).getStack();
                if (stack.isOf(net.minecraft.item.Items.AIR)) {
                    this.delayCounter = 2;
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
                    if (fishHook.getSlot(i).getStack().isOf(this.getSelectedItem())) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.QUICK_MOVE, mc.player);
                        this.delayCounter = 10;
                        return;
                    }
                }
                this.delayCounter = 40;
                mc.player.closeHandledScreen();
                return;
            } else if (((GenericContainerScreenHandler) fishHook).getRows() == 4) {
                final int b = InventoryUtil.getSlot(net.minecraft.item.Items.AIR);
                if (b <= 0) {
                    mc.player.closeHandledScreen();
                    this.delayCounter = 10;
                    return;
                }
                if (this.isSlotProcessed && b == 36) {
                    this.isSlotProcessed = false;
                    mc.player.closeHandledScreen();
                    return;
                }
                final Item j = this.getSelectedItem();
                while (true) {
                    final int n2 = 36;
                    final Item item2 = mc.player.currentScreenHandler.getStacks().get(n2).getItem();
                    if (item2 != net.minecraft.item.Items.AIR && item2 == j) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, n2, 1, SlotActionType.QUICK_MOVE, mc.player);
                        this.delayCounter = this.delay.getIntValue();
                        if (this.delay.getIntValue() != 0) {
                            break;
                        }
                        continue;
                    }
                }
                return;
            } else if (((GenericContainerScreenHandler) fishHook).getRows() == 3) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 15, 1, SlotActionType.QUICK_MOVE, mc.player);
                this.delayCounter = 10;
                return;
            }
        }
        this.delayCounter = 20;
    }

    private Item getSelectedItem() {
        final Enum a = this.item.getValue();
        if (a == Items.ALL) {
            for (int i = 0; i < 35; ++i) {
                final ItemStack stack = mc.player.getInventory().getStack(i);
                if (!stack.isOf(net.minecraft.item.Items.AIR)) {
                    return stack.getItem();
                }
            }
            return net.minecraft.item.Items.AIR;
        }
        final int n = a.ordinal() ^ 0x7F1F7668;
        int n2;
        if (n != 0) {
            n2 = ((n * 31 >>> 4) % n ^ n >>> 16);
        } else {
            n2 = 0;
        }
        Item item = null;
        switch (n2) {
            case 105679472: {
                item = net.minecraft.item.Items.PUMPKIN;
                break;
            }
            case 105679476: {
                item = net.minecraft.item.Items.SWEET_BERRIES;
                break;
            }
            case 105679474: {
                item = net.minecraft.item.Items.BAMBOO;
                break;
            }
            default: {
                item = net.minecraft.item.Items.AIR;
                break;
            }
            case 105679470: {
                item = net.minecraft.item.Items.BONE;
                break;
            }
            case 105679478: {
                item = net.minecraft.item.Items.SEA_PICKLE;
                break;
            }
        }
        return item;
    }

    private String getOrderCommand() {
        final Item j = this.getSelectedItem();
        if (j.equals(net.minecraft.item.Items.BONE)) {
            return "Bones";
        }
        return j.getName().getString();
    }

    enum Items {
        SEAPICKLE("Sea_Pickle", 0),
        SWEETBERRIES("Sweet_Berries", 1),
        BAMBOO("Bamboo", 2),
        PUMPKIN("Pumpkin", 3),
        BONE("Bone", 4),
        ALL("All", 5);

        Items(final String name, final int ordinal) {
        }
    }

    public enum Mode {
        SELL("Sell", 0),
        ORDER("Order", 1);

        Mode(final String name, final int ordinal) {
        }
    }


}
