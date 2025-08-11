package com.h1ggsk.radon.module;

import com.h1ggsk.radon.Radon;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.KeyEvent;
import com.h1ggsk.radon.module.modules.client.SelfDestruct;
import com.h1ggsk.radon.module.modules.combat.*;
import com.h1ggsk.radon.module.modules.donut.*;
import com.h1ggsk.radon.module.modules.misc.*;
import com.h1ggsk.radon.module.modules.movement.AntiHunger;
import com.h1ggsk.radon.module.modules.movement.Flight;
import com.h1ggsk.radon.module.modules.movement.NoFall;
import com.h1ggsk.radon.module.modules.render.*;
import com.h1ggsk.radon.module.setting.BindSetting;
import com.h1ggsk.radon.utils.EncryptedString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.h1ggsk.radon.Radon.mc;

public final class ModuleManager {
    public final List<Module> modules;

    public ModuleManager() {
        this.modules = new ArrayList<>();
        this.init();
        this.initKeybinds();
    }

    public void init() {
        this.addModule(new ElytraSwap());
        this.addModule(new MaceSwap());
        this.addModule(new Hitbox());
        this.addModule(new StaticHitboxes());
        this.addModule(new AutoTotem());
        this.addModule(new HoverTotem());
        this.addModule(new AutoInventoryTotem());
        this.addModule(new AnchorMacro());
        this.addModule(new AutoCrystal());
        this.addModule(new DoubleAnchor());
        this.addModule(new KillauraLegit());
        this.addModule(new AntiHunger());
        this.addModule(new Flight());
        this.addModule(new NoFall());
        this.addModule(new FastPlace());
        this.addModule(new Freecam());
        this.addModule(new AntiConsume());
        this.addModule(new AutoFirework());
        this.addModule(new ElytraGlide());
        this.addModule(new AutoTool());
        this.addModule(new AutoEat());
        this.addModule(new AutoMine());
        this.addModule(new CordSnapper());
        this.addModule(new KeyPearl());
        this.addModule(new NameProtect());
        this.addModule(new AutoTPA());
        this.addModule(new AutoMap());
        this.addModule(new RtpBaseFinder());
        this.addModule(new TunnelBaseFinder());
        this.addModule(new BoneDropper());
        this.addModule(new AutoSell());
        this.addModule(new ShulkerDropper());
        this.addModule(new AntiTrap());
        this.addModule(new AuctionSniper());
        this.addModule(new AutoSpawnerSell());
        this.addModule(new NoFluidOverlay());
        this.addModule(new HUD());
        this.addModule(new NethFinder());
        this.addModule(new PlayerESP());
        this.addModule(new StorageESP());
        this.addModule(new TargetHUD());
        this.addModule(new Fullbright());
        this.addModule(new com.h1ggsk.radon.module.modules.client.Radon());
        this.addModule(new SelfDestruct());
    }

    public List<Module> getEnabledModules() {
        return this.modules.stream().filter(Module::isEnabled).toList();
    }

    public List<Module> getModules() {
        return this.modules;
    }

    public void initKeybinds() {
        Radon.INSTANCE.getEventBus().register(this);
        for (final Module next : this.modules) {
            next.addSetting(new BindSetting(EncryptedString.of("Keybind"), next.getKeybind(), true).setDescription(EncryptedString.of("Key to enabled the module")));
        }
    }

    public List<Module> getModulesFromCategory(final Category category) {
        return this.modules.stream().filter(module -> module.getCategory() == category).toList();
    }

    public Module getModuleByClass(final Class<? extends Module> obj) {
        Objects.requireNonNull(obj);
        return this.modules.stream().filter(obj::isInstance).findFirst().orElse(null);
    }

    public void addModule(final Module module) {
        Radon.INSTANCE.getEventBus().register(module);
        this.modules.add(module);
    }

    @EventListener
    public void keybindListener(final KeyEvent keyEvent) {
        if (Radon.mc.player == null || Radon.mc.currentScreen instanceof ChatScreen || Radon.mc.currentScreen instanceof SignEditScreen || Radon.mc.currentScreen instanceof HangingSignEditScreen || Radon.mc.currentScreen instanceof AnvilScreen) {
            return;
        }

        if (!SelfDestruct.isActive) {
            this.modules.forEach(module -> {
                if (module.getKeybind() == keyEvent.key && keyEvent.mode == 1) {
                    module.toggle();
                }
            });
        }

        if (mc.currentScreen != null || mc.getOverlay() != null) return;
        String prefix = com.h1ggsk.radon.module.modules.client.Radon.commandPrefix.getValue();
        if (prefix == null || prefix.isEmpty()) return;
        char c = prefix.charAt(0);
        if (keyEvent.mode == 1 && keyEvent.key == (int) c) {
            keyEvent.cancel();
            ChatScreen chat = new ChatScreen(prefix);
            mc.setScreen(chat);
        }
    }
}
