package com.h1ggsk.radon.module.modules.misc;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.ModeSetting;
import com.h1ggsk.radon.module.setting.MinMaxSetting;
import com.h1ggsk.radon.module.setting.StringSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public final class AutoTPA extends Module {
    private final MinMaxSetting delayRange = new MinMaxSetting(EncryptedString.of("Delay"), 1.0, 80.0, 1.0, 10.0, 30.0);
    private final ModeSetting<Mode> mode = new ModeSetting<>(EncryptedString.of("Mode"), Mode.TPAHERE, Mode.class);
    private final StringSetting playerName = new StringSetting(EncryptedString.of("Player"), "DrDonutt");
    private int delayCounter;

    public AutoTPA() {
        super(EncryptedString.of("AutoTPA"), EncryptedString.of("Module that helps you teleport streamers to you"), -1, Category.MISC);
        this.addSettings(this.mode, this.delayRange, this.playerName);
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
        if (this.delayCounter > 0) {
            --this.delayCounter;
            return;
        }
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        String commandPrefix;
        if (this.mode.getValue().equals(Mode.TPA)) {
            commandPrefix = "tpa ";
        } else {
            commandPrefix = "tpahere ";
        }
        networkHandler.sendChatCommand(commandPrefix + this.playerName.getValue());
        this.delayCounter = this.delayRange.getRandomIntInRange();
    }

    enum Mode {
        TPA("Tpa", 0),
        TPAHERE("Tpahere", 1);

        Mode(final String name, final int ordinal) {
        }
    }
}