package com.h1ggsk.radon.utils;

import static com.h1ggsk.radon.Radon.mc;

public class DimensionUtils {
    public static Dimension getDimension() {
        if (mc == null) return Dimension.Overworld;
        if (mc.world == null) return Dimension.Overworld;

        return switch (mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.Nether;
            case "the_end" -> Dimension.End;
            default -> Dimension.Overworld;
        };
    }
}
