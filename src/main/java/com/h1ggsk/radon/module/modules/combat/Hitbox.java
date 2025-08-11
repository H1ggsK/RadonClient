package com.h1ggsk.radon.module.modules.combat;

import net.minecraft.entity.player.PlayerEntity;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.TargetMarginEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public final class Hitbox extends Module {
    private final NumberSetting expand = new NumberSetting(EncryptedString.of("Expand"), 0.0, 2.0, 0.5, 0.05);
    private final BooleanSetting enableRender = new BooleanSetting("Enable Render", true);

    public Hitbox() {
        super(EncryptedString.of("Hitboxes"), EncryptedString.of("Expands keybindListener player's hitbox."), -1, Category.COMBAT);
        this.addSettings(this.enableRender, this.expand);
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
    public void onTargetMargin(final TargetMarginEvent targetMarginEvent) {
        if (targetMarginEvent.entity instanceof PlayerEntity) {
            targetMarginEvent.cir.setReturnValue((float) this.expand.getValue());
        }
    }

    public double getHitboxExpansion() {
        if (!this.enableRender.getValue()) {
            return 0.0;
        }
        return this.expand.getValue();
    }
}