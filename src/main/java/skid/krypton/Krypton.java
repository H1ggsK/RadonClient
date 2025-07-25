package skid.krypton;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skid.krypton.gui.ClickGUI;
import skid.krypton.manager.ConfigManager;
import skid.krypton.manager.EventManager;
import skid.krypton.module.ModuleManager;
import skid.krypton.utils.rotation.RotationFaker;

import java.io.File;

public final class Krypton {
    public static ConfigManager configManager;
    public ModuleManager MODULE_MANAGER;
    public EventManager EVENT_BUS;
    public static MinecraftClient mc;
    public String version;
    public static Krypton INSTANCE;
    public boolean shouldPreventClose;
    public ClickGUI GUI;
    public Screen screen;
    public long modified;
    public File jar;
    public static Logger LOG;
    public static RotationFaker rotationFaker;

    public Krypton() {
        try {
            Krypton.INSTANCE = this;
            this.version = " b1.3";
            this.screen = null;
            this.EVENT_BUS = new EventManager();
            this.MODULE_MANAGER = new ModuleManager();
            this.GUI = new ClickGUI();
            configManager = new ConfigManager();
            this.getConfigManager().loadProfile();
            this.jar = new File(Krypton.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            this.modified = this.jar.lastModified();
            this.shouldPreventClose = false;
            LOG = LoggerFactory.getLogger(Krypton.class);
            rotationFaker = new RotationFaker();
            Krypton.mc = MinecraftClient.getInstance();
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
        }
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public ModuleManager getModuleManager() {
        return this.MODULE_MANAGER;
    }

    public EventManager getEventBus() {
        return this.EVENT_BUS;
    }

    public void resetModifiedDate() {
        this.jar.setLastModified(this.modified);
    }


    public static void info(String s) {
        LOG.info(s);
    }

    public static void warn(String s) {
        LOG.warn(s);
    }

    public static void error(String s) {
        LOG.error(s);
    }

    public RotationFaker getRotationFaker() {
        return rotationFaker;
    }
}
