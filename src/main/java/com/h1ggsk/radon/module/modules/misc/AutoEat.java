package com.h1ggsk.radon.module.modules.misc;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.mixin.MinecraftClientAccessor;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.InventoryUtil;

public final class AutoEat extends Module {
    private final NumberSetting healthThreshold = new NumberSetting(EncryptedString.of("Health Threshold"), 0.0, 19.0, 17.0, 1.0);
    private final NumberSetting hungerThreshold = new NumberSetting(EncryptedString.of("Hunger Threshold"), 0.0, 19.0, 19.0, 1.0);
    public boolean isEating;
    private int selectedFoodSlot;
    private int previousSelectedSlot;

    public AutoEat() {
        super(EncryptedString.of("AutoEat"), EncryptedString.of("It detects whenever the hungerbar/health falls keybindListener certain threshold, selects food in your hotbar, and starts eating."), -1, Category.MISC);
        this.addSettings(this.healthThreshold, this.hungerThreshold);
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
    public void onTick(final StartTickEvent startTickEvent) {
        if (this.isEating) {
            if (this.shouldEat()) {
                if (mc.player.getInventory().getStack(this.selectedFoodSlot).get(DataComponentTypes.FOOD) != null) {
                    final int bestFoodSlot = this.findBestFoodSlot();
                    if (bestFoodSlot == -1) {
                        this.stopEating();
                        return;
                    }
                    this.selectSlot(bestFoodSlot);
                }
                this.startEating();
            } else {
                this.stopEating();
            }
        } else if (this.shouldEat()) {
            this.selectedFoodSlot = this.findBestFoodSlot();
            if (this.selectedFoodSlot != -1) {
                this.saveCurrentSlot();
            }
        }
    }

    public boolean shouldEat() {
        final boolean b = mc.player.getHealth() <= this.healthThreshold.getIntValue();
        final boolean b2 = mc.player.getHungerManager().getFoodLevel() <= this.hungerThreshold.getIntValue();
        return this.findBestFoodSlot() != -1 && (b || b2);
    }

    private int findBestFoodSlot() {
        int n = -1;
        int n2 = -1;
        for (int i = 0; i < 9; ++i) {
            final Object value = mc.player.getInventory().getStack(i).getItem().getComponents().get(DataComponentTypes.FOOD);
            if (value != null) {
                final int nutrition = ((FoodComponent) value).nutrition();
                if (nutrition > n2) {
                    n = i;
                    n2 = nutrition;
                }
            }
        }
        return n;
    }

    private void saveCurrentSlot() {
        this.previousSelectedSlot = mc.player.getInventory().getSelectedSlot();
        this.startEating();
    }

    private void startEating() {
        this.selectSlot(this.selectedFoodSlot);
        this.setUseKeyPressed(true);
        if (!mc.player.isUsingItem()) {
            ((MinecraftClientAccessor) mc).invokeDoItemUse();
        }
        this.isEating = true;
    }

    private void stopEating() {
        this.selectSlot(this.previousSelectedSlot);
        this.setUseKeyPressed(false);
        this.isEating = false;
    }

    private void setUseKeyPressed(final boolean pressed) {
        mc.options.useKey.setPressed(pressed);
    }

    private void selectSlot(final int f) {
        InventoryUtil.swap(f);
        this.selectedFoodSlot = f;
    }
}
