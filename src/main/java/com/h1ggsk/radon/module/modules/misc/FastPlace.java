package com.h1ggsk.radon.module.modules.misc;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.PostItemUseEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public class FastPlace extends Module {
    private final BooleanSetting onlyXP = new BooleanSetting("Only XP", false);
    private final BooleanSetting allowBlocks = new BooleanSetting("Blocks", true);
    private final BooleanSetting allowItems = new BooleanSetting("Items", true);
    private final NumberSetting useDelay = new NumberSetting("Delay", 0.0, 10.0, 0.0, 1.0);

    public FastPlace() {
        super(EncryptedString.of("Fast Place"), EncryptedString.of("Spams use action."), -1, Category.MISC);
        this.addSettings(this.onlyXP, this.allowBlocks, this.allowItems, this.useDelay);
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
    public void onPostItemUse(final PostItemUseEvent postItemUseEvent) {
        final ItemStack getMainHandStack = mc.player.getMainHandStack();
        final ItemStack getItemUseTime = mc.player.getOffHandStack();
        final Item item = getMainHandStack.getItem();
        final Item item2 = mc.player.getOffHandStack().getItem();
        if (!getMainHandStack.isOf(Items.EXPERIENCE_BOTTLE) && !getItemUseTime.isOf(Items.EXPERIENCE_BOTTLE) && this.onlyXP.getValue()) {
            return;
        }
        if (!this.onlyXP.getValue()) {
            if (item instanceof BlockItem || item2 instanceof BlockItem) {
                if (!this.allowBlocks.getValue()) {
                    return;
                }
            } else if (!this.allowItems.getValue()) {
                return;
            }
        }
        if (item.getComponents().get(DataComponentTypes.FOOD) != null) {
            return;
        }
        if (item2.getComponents().get(DataComponentTypes.FOOD) != null) {
            return;
        }
        if (getMainHandStack.isOf(Items.RESPAWN_ANCHOR) || getMainHandStack.isOf(Items.GLOWSTONE) || getItemUseTime.isOf(Items.RESPAWN_ANCHOR) || getItemUseTime.isOf(Items.GLOWSTONE)) {
            return;
        }
        if (item instanceof RangedWeaponItem || item2 instanceof RangedWeaponItem) {
            return;
        }
        postItemUseEvent.cooldown = this.useDelay.getIntValue();
    }
}
