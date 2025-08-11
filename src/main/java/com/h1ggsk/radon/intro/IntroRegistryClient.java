package com.h1ggsk.radon.intro;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IntroRegistryClient
implements ClientModInitializer {
    public static final Identifier INTRO_MUSIC_ID = Identifier.of("radon", "intro_music");
    public static final SoundEvent INTRO_MUSIC_EVENT = SoundEvent.of(INTRO_MUSIC_ID);

    public void onInitializeClient() {
    }
}
