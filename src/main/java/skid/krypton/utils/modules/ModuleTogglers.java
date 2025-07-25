package skid.krypton.utils.modules;

import skid.krypton.Main;
import skid.krypton.module.modules.donut.OreSim;

public class ModuleTogglers {
    public static void toggleOreSim() {
        OreSim oreSim = (OreSim) Main.getKrypton().getModuleManager().getModuleByClass(OreSim.class);
        oreSim.toggle();
        oreSim.toggle();
    }
}
