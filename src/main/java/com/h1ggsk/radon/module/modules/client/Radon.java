package com.h1ggsk.radon.module.modules.client;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.PacketReceiveEvent;
import com.h1ggsk.radon.gui.ClickGUI;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.ModeSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.module.setting.StringSetting;
import com.h1ggsk.radon.utils.EncryptedString;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public final class Radon extends Module {
    public static final NumberSetting redColor = new NumberSetting(EncryptedString.of("Red"), 0.0, 255.0, 120.0, 1.0);
    public static final NumberSetting greenColor = new NumberSetting(EncryptedString.of("Green"), 0.0, 255.0, 190.0, 1.0);
    public static final NumberSetting blueColor = new NumberSetting(EncryptedString.of("Blue"), 0.0, 255.0, 255.0, 1.0);
    public static final NumberSetting windowAlpha = new NumberSetting(EncryptedString.of("Window Alpha"), 0.0, 255.0, 170.0, 1.0);
    public static final BooleanSetting enableBreathingEffect = new BooleanSetting(EncryptedString.of("Breathing"), false).setDescription(EncryptedString.of("Color breathing effect (only with rainbow off)"));
    public static final BooleanSetting enableRainbowEffect = new BooleanSetting(EncryptedString.of("Rainbow"), false).setDescription(EncryptedString.of("Enables LGBTQ mode"));
    public static final BooleanSetting renderBackground = new BooleanSetting(EncryptedString.of("Background"), true).setDescription(EncryptedString.of("Renders the background of the Click Gui"));
    public static final BooleanSetting useCustomFont = new BooleanSetting(EncryptedString.of("Custom Font"), true);
    private final BooleanSetting preventClose = new BooleanSetting(EncryptedString.of("Prevent Close"), true).setDescription(EncryptedString.of("For servers with freeze plugins that don't let you open the GUI"));
    public static final NumberSetting cornerRoundness = new NumberSetting(EncryptedString.of("Roundness"), 1.0, 10.0, 5.0, 1.0);
    public static final ModeSetting<AnimationMode> animationMode = new ModeSetting<>(EncryptedString.of("Animations"), AnimationMode.NORMAL, AnimationMode.class);
    public static final BooleanSetting enableMSAA = new BooleanSetting(EncryptedString.of("MSAA"), true).setDescription(EncryptedString.of("Anti Aliasing | This can impact performance if you're using tracers but gives them keybindListener smoother look |"));
    public static final StringSetting commandPrefix = new StringSetting("Command Prefix", ".");
    public static final BooleanSetting showIntro = new BooleanSetting("Intro Animation", true);
    public boolean shouldPreventClose;

    public Radon() {
        super(EncryptedString.of("Radon"), EncryptedString.of("Settings for the client"), 344, Category.CLIENT);
        this.addSettings(Radon.redColor, Radon.greenColor, Radon.blueColor, Radon.windowAlpha, Radon.renderBackground, this.preventClose, Radon.cornerRoundness, Radon.animationMode, Radon.enableMSAA, Radon.commandPrefix, Radon.showIntro);
    }

    @Override
    public void onEnable() {
        com.h1ggsk.radon.Radon.INSTANCE.screen = mc.currentScreen;
        if (com.h1ggsk.radon.Radon.INSTANCE.GUI != null) {
            mc.setScreenAndRender(com.h1ggsk.radon.Radon.INSTANCE.GUI);
        } else if (mc.currentScreen instanceof InventoryScreen) {
            shouldPreventClose = true;
        }
        if (new Random().nextInt(3) == 1) {
            CompletableFuture.runAsync(() -> {
            });
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen instanceof ClickGUI) {
            com.h1ggsk.radon.Radon.INSTANCE.GUI.close();
            mc.setScreenAndRender(com.h1ggsk.radon.Radon.INSTANCE.screen);
            com.h1ggsk.radon.Radon.INSTANCE.GUI.onGuiClose();
        } else if (mc.currentScreen instanceof InventoryScreen) {
            shouldPreventClose = false;
        }
        super.onDisable();
    }

    @EventListener
    public void onPacketReceive(final PacketReceiveEvent packetReceiveEvent) {
        if (shouldPreventClose && packetReceiveEvent.packet instanceof OpenScreenS2CPacket && this.preventClose.getValue()) {
            packetReceiveEvent.cancel();
        }
    }

    public enum AnimationMode {
        NORMAL("Normal", 0),
        POSITIVE("Positive", 1),
        OFF("Off", 2);

        AnimationMode(final String name, final int ordinal) {
        }
    }

}