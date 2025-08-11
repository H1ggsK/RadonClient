package com.h1ggsk.radon.utils;

public final class MathUtil {
    public static double roundToNearest(final double value, final double step) {
        return step * Math.round(value / step);
    }

    public static double smoothStep(final double factor, final double start, final double end) {
        final double max = Math.max(0.0, Math.min(1.0, factor));
        return start + (end - start) * (max * max * (3.0 - 2.0 * max));
    }

    public static double approachValue(final float speed, final double current, final double target) {
        final double ceil = Math.ceil(Math.abs(target - current) * speed);
        if (current < target) {
            return Math.min(current + (int) ceil, target);
        }
        return Math.max(current - (int) ceil, target);
    }

    public static double linearInterpolate(final double factor, final double start, final double end) {
        return start + (end - start) * factor;
    }

    public static double exponentialInterpolate(final double start, final double end, final double base, final double exponent) {
        return linearInterpolate(1.0f - (float) Math.pow(base, exponent), start, end);
    }

    public static double clampValue(final double value, final double min, final double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static int clampInt(final int value, final int min, final int max) {
        return Math.max(min, Math.min(value, max));
    }
}