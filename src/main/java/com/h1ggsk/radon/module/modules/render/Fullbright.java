package com.h1ggsk.radon.module.modules.render;

import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.ModeSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.EncryptedString;
import net.minecraft.world.LightType;

public final class Fullbright extends Module {
    private final NumberSetting minimumLightLevel = new NumberSetting(EncryptedString.of("Light Level"), 0.0, 15.0, 15.0, 1.0);
    private final ModeSetting<LightType> lightType = new ModeSetting<>("Light Type", LightType.BLOCK, LightType.class);

    public Fullbright() {
        super(EncryptedString.of("Fullbright"), EncryptedString.of("Lights up your world!"), -1, Category.RENDER);
        this.addSettings(this.minimumLightLevel, this.lightType);
    }

    @Override
    public void onEnable() {
        try { mc.worldRenderer.reload(); } catch (Exception ignored) {}
    }

    @Override
    public void onDisable() {
        try { mc.worldRenderer.reload(); } catch (Exception ignored) {}
    }

    public int getLuminance(LightType type) {
        if (!isEnabled() || type != lightType.getValue()) {
            return 0;
        }
        return minimumLightLevel.getIntValue();
    }

}