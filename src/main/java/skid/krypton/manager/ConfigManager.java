package skid.krypton.manager;

import com.google.gson.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import skid.krypton.Krypton;
import skid.krypton.module.Module;
import skid.krypton.module.setting.*;
import java.io.*;

public final class ConfigManager {
    private static final File CONFIG_FILE = new File("config/krypton.json");
    private final Gson gson = new Gson();
    private JsonObject jsonObject;

    public void loadProfile() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();

            if (jsonObject == null) {
                if (CONFIG_FILE.exists()) {
                    try (Reader reader = new FileReader(CONFIG_FILE)) {
                        jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                    }
                } else {
                    jsonObject = new JsonObject();
                }
            }

            for (Module module : Krypton.INSTANCE.getModuleManager().c()) {
                try {
                    JsonElement moduleElem = jsonObject.get(module.getName().toString());
                    if (moduleElem == null || !moduleElem.isJsonObject()) continue;

                    JsonObject modObj = moduleElem.getAsJsonObject();

                    JsonElement enabledElem = modObj.get("enabled");
                    if (enabledElem != null && enabledElem.isJsonPrimitive() && enabledElem.getAsBoolean()) {
                        module.toggle(true);
                    }

                    for (Setting setting : module.getSettings()) {
                        JsonElement settingElem = modObj.get(setting.getName().toString());
                        if (settingElem != null) {
                            setValueFromJson(setting, settingElem, module);
                        }
                    }
                } catch (Exception moduleEx) {
                    System.err.println("Error loading module '" + module.getName() + "': " + moduleEx.getMessage());
                    moduleEx.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading profile: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void setValueFromJson(Setting setting, JsonElement jsonElement, Module module) {
        try {
            if (setting instanceof BooleanSetting bs) {
                if (jsonElement.isJsonPrimitive()) {
                    bs.setValue(jsonElement.getAsBoolean());
                }
            } else if (setting instanceof ModeSetting<?> ms) {
                if (jsonElement.isJsonPrimitive()) {
                    int idx = jsonElement.getAsInt();
                    ms.setModeIndex(idx != -1 ? idx : ms.getOriginalValue());
                }
            } else if (setting instanceof NumberSetting ns) {
                if (jsonElement.isJsonPrimitive()) {
                    ns.setValue(jsonElement.getAsDouble());
                }
            } else if (setting instanceof BindSetting bind) {
                if (jsonElement.isJsonPrimitive()) {
                    int key = jsonElement.getAsInt();
                    bind.setValue(key);
                    if (bind.isModuleKey()) {
                        module.setKeybind(key);
                    }
                }
            } else if (setting instanceof StringSetting ss) {
                if (jsonElement.isJsonPrimitive()) {
                    ss.setValue(jsonElement.getAsString());
                }
            } else if (setting instanceof MinMaxSetting mm) {
                if (jsonElement.isJsonObject()) {
                    JsonObject obj = jsonElement.getAsJsonObject();
                    if (obj.has("min") && obj.has("max")) {
                        mm.setCurrentMin(obj.get("min").getAsDouble());
                        mm.setCurrentMax(obj.get("max").getAsDouble());
                    }
                }
            } else if (setting instanceof ItemSetting is && jsonElement.isJsonPrimitive()) {
                is.setItem(Registries.ITEM.get(Identifier.of(jsonElement.getAsString())));
            }
        } catch (Exception ignored) {
            System.err.println("Error loading setting '" + setting.getName() + "': " + ignored.getMessage());
        }
    }

    public void shutdown() {
        try {
            JsonObject out = new JsonObject();
            for (Module module : Krypton.INSTANCE.getModuleManager().c()) {
                try {
                    JsonObject modObj = new JsonObject();
                    modObj.addProperty("enabled", module.isEnabled());
                    for (Setting setting : module.getSettings()) {
                        save(setting, modObj, module);
                    }
                    out.add(module.getName().toString(), modObj);
                } catch (Exception moduleEx) {
                    System.err.println("Error saving module '" + module.getName() + "': " + moduleEx.getMessage());
                    moduleEx.printStackTrace();
                }
            }
            try (Writer writer = new FileWriter(CONFIG_FILE)) {
                gson.toJson(out, writer);
            }
        } catch (Exception ex) {
            System.err.println("Error saving config: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void save(Setting setting, JsonObject jsonObject, Module module) {
        try {
            String name = setting.getName().toString();
            if (setting instanceof BooleanSetting bs) {
                jsonObject.addProperty(name, bs.getValue());
            } else if (setting instanceof ModeSetting<?> ms) {
                jsonObject.addProperty(name, ms.getModeIndex());
            } else if (setting instanceof NumberSetting ns) {
                jsonObject.addProperty(name, ns.getValue());
            } else if (setting instanceof BindSetting bind) {
                jsonObject.addProperty(name, bind.getValue());
            } else if (setting instanceof StringSetting ss) {
                jsonObject.addProperty(name, ss.getValue());
            } else if (setting instanceof MinMaxSetting mm) {
                JsonObject range = new JsonObject();
                range.addProperty("min", mm.getCurrentMin());
                range.addProperty("max", mm.getCurrentMax());
                jsonObject.add(name, range);
            } else if (setting instanceof ItemSetting is) {
                jsonObject.addProperty(name, Registries.ITEM.getId(is.getItem()).toString());
            }
        } catch (Exception ex) {
            System.err.println("Error saving setting '" + setting.getName() + "': " + ex.getMessage());
        }
    }
}
