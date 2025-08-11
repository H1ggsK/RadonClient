package com.h1ggsk.radon.utils;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import com.h1ggsk.radon.Radon;
import com.h1ggsk.radon.mixin.ClientPlayerInteractionManagerAccessor;

import java.util.function.Predicate;

public final class InventoryUtil {
    public static void swap(final int selectedSlot) {
        if (selectedSlot < 0 || selectedSlot > 8) {
            return;
        }
        Radon.mc.player.getInventory().setSelectedSlot(selectedSlot);
        ((ClientPlayerInteractionManagerAccessor) Radon.mc.interactionManager).syncSlot();
    }

    public static boolean swapStack(final Predicate<ItemStack> predicate) {
        final PlayerInventory getInventory = Radon.mc.player.getInventory();
        for (int i = 0; i < 9; ++i) {
            if (predicate.test(getInventory.getStack(i))) {
                getInventory.setSelectedSlot(i);
                return true;
            }
        }
        return false;
    }

    public static boolean swapItem(final Predicate<Item> predicate) {
        final PlayerInventory getInventory = Radon.mc.player.getInventory();
        for (int i = 0; i < 9; ++i) {
            if (predicate.test(getInventory.getStack(i).getItem())) {
                getInventory.setSelectedSlot(i);
                return true;
            }
        }
        return false;
    }

    public static boolean swap(Item item) {
        return InventoryUtil.swapItem((Item item2) -> item2 == item);
    }

    public static int getSlot(final Item obj) {
        final ScreenHandler currentScreenHandler = Radon.mc.player.currentScreenHandler;
        if (Radon.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            int n = 0;
            for (int i = 0; i < ((GenericContainerScreenHandler) Radon.mc.player.currentScreenHandler).getRows() * 9; ++i) {
                if (currentScreenHandler.getSlot(i).getStack().getItem().equals(obj)) {
                    ++n;
                }
            }
            return n;
        }
        return 0;
    }
}