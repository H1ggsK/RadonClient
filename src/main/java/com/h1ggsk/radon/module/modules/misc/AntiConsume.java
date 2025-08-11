package com.h1ggsk.radon.module.modules.misc;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.EndTickEvent;
import com.h1ggsk.radon.event.events.InteractItemEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;

import java.util.ArrayList;
import java.util.List;

public final class AntiConsume extends Module {
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0.0, 20.0, 0.0, 1.0);
    private final BooleanSetting swingHand = new BooleanSetting(EncryptedString.of("Swing Hand"), true);
    private final BooleanSetting playSound = new BooleanSetting(EncryptedString.of("Play Sound"), true);
    private final NumberSetting fireworkLevel = new NumberSetting(EncryptedString.of("Firework Duration"), 0, 25, 3, 1);

    private final List<FireworkRocketEntity> fireworks = new ArrayList<>();

    public AntiConsume() {
        super(EncryptedString.of("Anti Consume"), EncryptedString.of("Conserves fireworks when used (does not consume)"), -1, Category.MISC);
        this.addSettings(delay, swingHand, playSound, fireworkLevel);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        fireworks.clear();
    }

    @EventListener
    private void onInteractItem(InteractItemEvent event) {
        ItemStack itemStack = mc.player.getStackInHand(event.hand);

        if (itemStack.getItem() instanceof FireworkRocketItem) {
            event.toReturn = ActionResult.PASS;
            if (swingHand.getValue()) {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            boost();
        }
    }

    @EventListener
    private void onTick(EndTickEvent event) {
        fireworks.removeIf(Entity::isRemoved);
    }

    private void boost() {
        if (!mc.player.isGliding() || mc.currentScreen != null) return;

        ItemStack itemStack = Items.FIREWORK_ROCKET.getDefaultStack();
        itemStack.set(DataComponentTypes.FIREWORKS, new FireworksComponent(fireworkLevel.getIntValue(), itemStack.get(DataComponentTypes.FIREWORKS).explosions()));

        FireworkRocketEntity entity = new FireworkRocketEntity(mc.world, itemStack, mc.player);
        fireworks.add(entity);

        if (playSound.getValue()) mc.world.playSoundFromEntity(mc.player, entity, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
        mc.world.addEntity(entity);
    }

    public boolean isFirework(FireworkRocketEntity firework) {
        return this.isEnabled() && fireworks.contains(firework);
    }
}