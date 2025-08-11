package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.module.modules.client.Radon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value = EnvType.CLIENT)
@Mixin(TitleScreen.class)
public class TitleScreenBackgroundMixin {

    @Unique
    private static final int FRAME_COUNT = 3;
    @Unique
    private static final int FRAME_DELAY_MS = 35;
    @Unique
    private static long lastFrameTime = 0;
    @Unique
    private static int currentFrame = 1;

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void renderCustomBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Radon.showIntro.getValue()) {
            MinecraftClient client = MinecraftClient.getInstance();
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastFrameTime >= FRAME_DELAY_MS) {
                currentFrame = (currentFrame % FRAME_COUNT) + 1;
                lastFrameTime = currentTime;
            }

            String frameName = String.format("background_%05d.png", currentFrame);
            Identifier frameTexture = Identifier.of("radon", "textures/background/" + frameName);

            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();
            context.drawTexture(RenderPipelines.GUI_OPAQUE_TEX_BG, frameTexture, 0, 0, 0.0f, 0.0f, width, height, width, height);

            ci.cancel();
        }
    }
}
