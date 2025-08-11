package com.h1ggsk.radon.utils.meteor;

public class Seed {
    public final Long seed;

    public Seed(Long seed) {
        this.seed = seed;
    }

    public static Seed of(Long seed) {
        return new Seed(seed);
    }

    public static Seed of(String seed) {
        try {
            return Seed.of(Long.parseLong(seed));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid seed: " + seed, e);
        }
    }
}