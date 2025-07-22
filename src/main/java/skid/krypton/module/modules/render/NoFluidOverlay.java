package skid.krypton.module.modules.render;

import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.module.setting.BooleanSetting;
import skid.krypton.utils.EncryptedString;

public final class NoFluidOverlay extends Module {
    public final BooleanSetting removeWater = new BooleanSetting(EncryptedString.of("Water"), true);
    public final BooleanSetting removeLava = new BooleanSetting(EncryptedString.of("Water"), true);

    public NoFluidOverlay() {
        super(EncryptedString.of("NoFluidOverlay"), EncryptedString.of("Removes the fluid overlay from water and fire!"), -1, Category.RENDER);
        this.addSettings(this.removeWater, this.removeLava);
    }

    public float getOverlayOffset()
    {
        return isEnabled() ? 0.6f : 0;
    }

}