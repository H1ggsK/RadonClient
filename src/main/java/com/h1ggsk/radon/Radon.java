package com.h1ggsk.radon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.h1ggsk.radon.commands.Commands;
import com.h1ggsk.radon.gui.ClickGUI;
import com.h1ggsk.radon.manager.ConfigManager;
import com.h1ggsk.radon.manager.EventManager;
import com.h1ggsk.radon.module.ModuleManager;
import com.h1ggsk.radon.utils.rotation.RotationFaker;

import java.io.File;

public final class Radon {
    public static ConfigManager configManager;
    public ModuleManager MODULE_MANAGER;
    public EventManager EVENT_BUS;
    public static MinecraftClient mc;
    public String version;
    public static Radon INSTANCE;
    public boolean shouldPreventClose;
    public ClickGUI GUI;
    public Screen screen;
    public long modified;
    public File jar;
    public static Logger LOG;
    public static RotationFaker rotationFaker;

    public Radon() {
        try {
            Radon.INSTANCE = this;
            this.version = " b1.3";
            this.screen = null;
            this.EVENT_BUS = new EventManager();
            this.MODULE_MANAGER = new ModuleManager();
            this.GUI = new ClickGUI();
            configManager = new ConfigManager();
            this.getConfigManager().loadProfile();
            this.jar = new File(Radon.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            this.modified = this.jar.lastModified();
            this.shouldPreventClose = false;
            LOG = LoggerFactory.getLogger(Radon.class);
            rotationFaker = new RotationFaker();
            Radon.mc = MinecraftClient.getInstance();
            Commands.init();
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
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
