package com.h1ggsk.radon.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.h1ggsk.radon.Radon;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.modules.combat.Hitbox;

@Mixin({EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {
    @ModifyVariable(method = {"renderHitbox"}, ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    private static Box onRenderHitboxEditBox(final Box box) {
        final Module hitboxes = Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(Hitbox.class);
        return hitboxes.isEnabled() ? box.expand(((Hitbox) hitboxes).getHitboxExpansion()) : box;
    }
}