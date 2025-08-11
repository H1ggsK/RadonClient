package com.h1ggsk.radon.module.modules.render;

import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public final class NoFluidOverlay extends Module {
    public final BooleanSetting removeWater = new BooleanSetting(EncryptedString.of("Water"), true);
    public final BooleanSetting removeLava = new BooleanSetting(EncryptedString.of("Lava"), true);

    public NoFluidOverlay() {
        super(EncryptedString.of("NoFluidOverlay"), EncryptedString.of("Removes the fluid overlay from water and fire!"), -1, Category.RENDER);
        this.addSettings(this.removeWater, this.removeLava);
    }

    public float getOverlayOffset()
    {
        return isEnabled() ? 0.6f : 0;
    }

}