package com.h1ggsk.radon.intro;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class IntroScreen extends Screen {
    private static final int totalFrames = 237;
    private static final double VIDEO_FPS = 2380.0 / 79.0;

    private long musicStartTime = -1;
    private boolean hasStartedMusic = false;

    public IntroScreen() {
        super(Text.literal("Intro Screen"));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        if (!hasStartedMusic) {
            hasStartedMusic = true;
            mc.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
            mc.getSoundManager().play(
                    PositionedSoundInstance.master(IntroRegistryClient.INTRO_MUSIC_EVENT, 1.0F), 0
            );
            musicStartTime = System.currentTimeMillis();
        }


        long elapsedMs = System.currentTimeMillis() - musicStartTime;
        double elapsedSeconds = elapsedMs / 1000.0;

        int currentFrame = (int) Math.floor(elapsedSeconds * VIDEO_FPS) + 1;

        if (currentFrame >= totalFrames) {
            mc.setScreen(new TitleScreen());
            return;
        }

        String frameNumber = String.format("%05d", currentFrame);
        Identifier frame = Identifier.of("radon", "textures/intro/intro_" + frameNumber + ".png");
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, frame, 0, 0, 0.0f, 0.0f, screenWidth, screenHeight, screenWidth, screenHeight);
        mc.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        MinecraftClient.getInstance().getSoundManager().stopAll();
        return true;
    }
}
