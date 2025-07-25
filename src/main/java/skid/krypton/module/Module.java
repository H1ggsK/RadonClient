package skid.krypton.module;

import net.minecraft.client.MinecraftClient;
import skid.krypton.Krypton;
import skid.krypton.manager.EventManager;
import skid.krypton.module.setting.Setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Module implements Serializable {
    private final List<Setting> settings;
    protected final EventManager EVENT_BUS;
    protected static MinecraftClient mc;
    private CharSequence name;
    private CharSequence description;
    private boolean enabled;
    private int keybind;
    private Category category;
    private final boolean i;

    public Module(final CharSequence name, final CharSequence description, final int keybind, final Category category) {
        this.settings = new ArrayList<Setting>();
        this.EVENT_BUS = Krypton.INSTANCE.getEventBus();
        mc = MinecraftClient.getInstance();
        this.i = false;
        this.name = name;
        this.description = description;
        this.enabled = false;
        this.keybind = keybind;
        this.category = category;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public CharSequence getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public CharSequence getDescription() {
        return this.description;
    }

    public int getKeybind() {
        return this.keybind;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

    public void setName(final CharSequence name) {
        this.name = name;
    }

    public void setDescription(final CharSequence description) {
        this.description = description;
    }

    public void setKeybind(final int keybind) {
        this.keybind = keybind;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void addSetting(final Setting setting) {
        this.settings.add(setting);
    }

    public void addSettings(final Setting... a) {
        this.settings.addAll(Arrays.asList(a));
    }

    public void toggle(final boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
