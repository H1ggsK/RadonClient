package skid.krypton.module.modules.client;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import skid.krypton.event.EventListener;
import skid.krypton.event.events.PacketReceiveEvent;
import skid.krypton.gui.ClickGUI;
import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.module.setting.BooleanSetting;
import skid.krypton.module.setting.ModeSetting;
import skid.krypton.module.setting.NumberSetting;
import skid.krypton.module.setting.StringSetting;
import skid.krypton.utils.EncryptedString;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public final class Krypton extends Module {
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
    public static final BooleanSetting enableMSAA = new BooleanSetting(EncryptedString.of("MSAA"), true).setDescription(EncryptedString.of("Anti Aliasing | This can impact performance if you're using tracers but gives them a smoother look |"));
    public static final StringSetting commandPrefix = new StringSetting("Command Prefix", ".");
    public boolean shouldPreventClose;

    public Krypton() {
        super(EncryptedString.of("Krypton+"), EncryptedString.of("Settings for the client"), 344, Category.CLIENT);
        this.addSettings(Krypton.redColor, Krypton.greenColor, Krypton.blueColor, Krypton.windowAlpha, Krypton.renderBackground, this.preventClose, Krypton.cornerRoundness, Krypton.animationMode, Krypton.enableMSAA, Krypton.commandPrefix);
    }

    @Override
    public void onEnable() {
        skid.krypton.Krypton.INSTANCE.screen = this.mc.currentScreen;
        if (skid.krypton.Krypton.INSTANCE.GUI != null) {
            this.mc.setScreenAndRender(skid.krypton.Krypton.INSTANCE.GUI);
        } else if (this.mc.currentScreen instanceof InventoryScreen) {
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
        if (this.mc.currentScreen instanceof ClickGUI) {
            skid.krypton.Krypton.INSTANCE.GUI.close();
            this.mc.setScreenAndRender(skid.krypton.Krypton.INSTANCE.screen);
            skid.krypton.Krypton.INSTANCE.GUI.onGuiClose();
        } else if (this.mc.currentScreen instanceof InventoryScreen) {
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