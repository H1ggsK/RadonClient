package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.module.modules.client.Radon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.h1ggsk.radon.intro.IntroScreen;

@Environment(value=EnvType.CLIENT)
@Mixin(value={TitleScreen.class})
public class StartVideoMixin {
    @Unique
    private static boolean shownVideo = false;

    @Inject(at={@At(value="TAIL")}, method={"init"})
    private void onInit(CallbackInfo ci) {
        if (!shownVideo) {
            shownVideo = true;
            if (Radon.showIntro.getValue()) {
                MinecraftClient.getInstance().setScreen(new IntroScreen());
            }
        }
    }
}