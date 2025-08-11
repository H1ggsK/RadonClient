package com.h1ggsk.radon.module.modules.misc;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BambooShootBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.AttackBlockEvent;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.InventoryUtil;
import net.minecraft.registry.tag.ItemTags;

import java.util.function.Predicate;

public final class AutoTool extends Module {
    private final BooleanSetting antiBreak = new BooleanSetting(EncryptedString.of("Anti Break"), true);
    private final NumberSetting antiBreakPercentage = new NumberSetting(EncryptedString.of("Anti Break Percentage"), 1.0, 100.0, 5.0, 1.0);
    private boolean isToolSwapping;
    private int keybindCounter;
    private int selectedToolSlot;

    public AutoTool() {
        super(EncryptedString.of("Auto Tool"), EncryptedString.of("Module that automatically switches to best tool"), -1, Category.MISC);
        this.addSettings(this.antiBreak, this.antiBreakPercentage);
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
        if (this.keybindCounter <= 0 && this.isToolSwapping && this.selectedToolSlot != -1) {
            InventoryUtil.swap(this.selectedToolSlot);
            this.isToolSwapping = false;
        } else {
            --this.keybindCounter;
        }
    }

    @EventListener
    public void handleAttackBlockEvent(final AttackBlockEvent attackBlockEvent) {
        final BlockState getBlockState = mc.world.getBlockState(attackBlockEvent.pos);
        final ItemStack getBlockEntity = mc.player.getMainHandStack();
        double n = -1.0;
        this.selectedToolSlot = -1;
        for (int i = 0; i < 9; ++i) {
            final double a = calculateToolEfficiency(mc.player.getInventory().getStack(i), getBlockState, itemStack -> !this.isToolBreakingSoon(itemStack));
            if (a >= 0.0) {
                if (a > n) {
                    this.selectedToolSlot = i;
                    n = a;
                }
            }
        }
        if ((this.selectedToolSlot != -1 && n > calculateToolEfficiency(getBlockEntity, getBlockState, itemStack2 -> !this.isToolBreakingSoon(itemStack2))) || this.isToolBreakingSoon(getBlockEntity) || !isToolItemStack(getBlockEntity)) {
            InventoryUtil.swap(this.selectedToolSlot);
        }
        final ItemStack mainHandStack = mc.player.getMainHandStack();
        if (this.isToolBreakingSoon(mainHandStack) && isToolItemStack(mainHandStack)) {
            mc.options.attackKey.setPressed(false);
            attackBlockEvent.cancel();
        }
    }

    public static double calculateToolEfficiency(final ItemStack itemStack, final BlockState blockState, final Predicate<ItemStack> predicate) {
        if (!predicate.test(itemStack) || !isToolItemStack(itemStack)) {
            return -1.0;
        }
        if (!itemStack.isSuitableFor(blockState) && (!(itemStack.isIn(ItemTags.SWORDS)) || (!(blockState.getBlock() instanceof BambooBlock) && !(blockState.getBlock() instanceof BambooShootBlock))) && (!(itemStack.getItem() instanceof ShearsItem) || !(blockState.getBlock() instanceof LeavesBlock)) && !blockState.isIn(BlockTags.WOOL)) {
            return -1.0;
        }
        return 0.0 + itemStack.getMiningSpeedMultiplier(blockState) * 1000.0f;
    }

    public static boolean isToolItemStack(final ItemStack itemStack) {
        return isToolItem(itemStack.getItem());
    }

    public static boolean isToolItem(final Item item) {
        return item.getDefaultStack().isIn(ConventionalItemTags.TOOLS) || item instanceof ShearsItem;
    }

    private boolean isToolBreakingSoon(final ItemStack itemStack) {
        return this.antiBreak.getValue() && itemStack.getMaxDamage() - itemStack.getDamage() < itemStack.getMaxDamage() * this.antiBreakPercentage.getIntValue() / 100;
    }
}
