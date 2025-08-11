package com.h1ggsk.radon;

import net.fabricmc.api.ModInitializer;

public final class Main implements ModInitializer {
    public static Radon radon;
    public void onInitialize() {
        radon = new Radon();
    }
    public static Radon getRadon() {
        return radon;
    }
}