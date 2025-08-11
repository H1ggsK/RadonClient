package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.Radon;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.h1ggsk.radon.module.modules.misc.NameProtect;

@Mixin({TextVisitFactory.class})
public class TextVisitFactoryMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", ordinal = 0), method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"}, index = 0)
    private static String adjustText(final String s) {
        final NameProtect nameprotect = (NameProtect) Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(NameProtect.class);
        return nameprotect.isEnabled() && s.contains(Radon.mc.getSession().getUsername()) ? s.replace(Radon.mc.getSession().getUsername(), nameprotect.getFakeName()) : s;
    }
}