package skid.krypton.mixin;

import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skid.krypton.Main;
import skid.krypton.module.modules.render.Fullbright;

@Mixin(value = LightDataAccess.class, remap = false)
public abstract class SodiumLightDataAccessMixin {
    @Unique
    private Fullbright fb;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        fb = (Fullbright) Main.getKrypton().getModuleManager().getModuleByClass(Fullbright.class);
    }

    @ModifyVariable(method = "compute", at = @At(value = "STORE"), name = "sl")
    private int compute_assignSL(int sl) {
        return fb != null && fb.isEnabled() ? Math.max(fb.getLuminance(LightType.SKY), sl) : sl;
    }

    @ModifyVariable(method = "compute", at = @At(value = "STORE"), name = "bl")
    private int compute_assignBL(int bl) {
        return fb != null && fb.isEnabled() ? Math.max(fb.getLuminance(LightType.BLOCK), bl) : bl;
    }
}