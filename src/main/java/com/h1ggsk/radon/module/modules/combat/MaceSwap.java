package com.h1ggsk.radon.module.modules.combat;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.AttackEvent;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EnchantmentUtil;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.InventoryUtil;
import net.minecraft.registry.tag.ItemTags;

public final class MaceSwap extends Module {
    private final BooleanSetting enableWindBurst = new BooleanSetting(EncryptedString.of("Wind Burst"), true);
    private final BooleanSetting enableBreach = new BooleanSetting(EncryptedString.of("Breach"), true);
    private final BooleanSetting onlySword = new BooleanSetting(EncryptedString.of("Only Sword"), false);
    private final BooleanSetting onlyAxe = new BooleanSetting(EncryptedString.of("Only Axe"), false);
    private final BooleanSetting switchBack = new BooleanSetting(EncryptedString.of("Switch Back"), true);
    private final NumberSetting switchDelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0.0, 20.0, 0.0, 1.0);
    private boolean isSwitching;
    private int previousSlot;
    private int currentSwitchDelay;

    public MaceSwap() {
        super(EncryptedString.of("Mace Swap"), EncryptedString.of("Switches to keybindListener mace when attacking."), -1, Category.COMBAT);
        this.addSettings(this.enableWindBurst, this.enableBreach, this.onlySword, this.onlyAxe, this.switchBack, this.switchDelay);
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
        if (mc.player == null) {
            return;
        }
        if (this.isSwitching) {
            if (this.switchBack.getValue()) {
                this.performSwitchBack();
            } else {
                this.resetState();
            }
        }
    }

    @EventListener
    public void onAttack(final AttackEvent attackEvent) {
        if (mc.player == null) {
            return;
        }
        if (!this.isValidWeapon()) {
            return;
        }
        if (this.previousSlot == -1) {
            this.previousSlot = mc.player.getInventory().getSelectedSlot();
        }
        if ((this.enableWindBurst.getValue() && this.enableBreach.getValue()) || (!this.enableWindBurst.getValue() && !this.enableBreach.getValue())) {
            InventoryUtil.swap(Items.MACE);
        } else {
            if (this.enableWindBurst.getValue()) {
                InventoryUtil.swapStack(itemStack -> EnchantmentUtil.hasEnchantment(itemStack, Enchantments.WIND_BURST));
            }
            if (this.enableBreach.getValue()) {
                InventoryUtil.swapStack(itemStack2 -> EnchantmentUtil.hasEnchantment(itemStack2, Enchantments.BREACH));
            }
        }
        this.isSwitching = true;
    }

    private boolean isValidWeapon() {
        final Item item = mc.player.getMainHandStack().getItem();
        if (this.onlySword.getValue() && this.onlyAxe.getValue()) {
            return item.getDefaultStack().isIn(ItemTags.SWORDS) || item instanceof AxeItem;
        }
        return (!this.onlySword.getValue() || item.getDefaultStack().isIn(ItemTags.SWORDS) && (!this.onlyAxe.getValue() || item instanceof AxeItem));
    }

    private void performSwitchBack() {
        if (this.currentSwitchDelay < this.switchDelay.getIntValue()) {
            ++this.currentSwitchDelay;
            return;
        }
        InventoryUtil.swap(this.previousSlot);
        this.resetState();
    }

    private void resetState() {
        this.previousSlot = -1;
        this.currentSwitchDelay = 0;
        this.isSwitching = false;
    }
}
