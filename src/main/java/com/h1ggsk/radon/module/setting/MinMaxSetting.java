package com.h1ggsk.radon.module.setting;

import java.util.Random;

public class MinMaxSetting extends Setting {
    private final double min;
    private final double max;
    private final double step;
    private final double defaultMin;
    private final double defaultMax;
    private double currentMin;
    private double currentMax;

    public MinMaxSetting(final CharSequence charSequence, final double min, final double max, final double step, final double defaultMin, final double defaultMax) {
        super(charSequence);
        this.min = min;
        this.max = max;
        this.step = step;
        this.currentMin = defaultMin;
        this.currentMax = defaultMax;
        this.defaultMin = defaultMin;
        this.defaultMax = defaultMax;
    }

    public int getMinInt() {
        return (int) this.currentMin;
    }

    public float getMinFloat() {
        return (float) this.currentMin;
    }

    public long getMinLong() {
        return (long) this.currentMin;
    }

    public int getMaxInt() {
        return (int) this.currentMax;
    }

    public float getMaxFloat() {
        return (float) this.currentMax;
    }

    public long getMaxLong() {
        return (long) this.currentMax;
    }

    public double getMinValue() {
        return this.min;
    }

    public double getMaxValue() {
        return this.max;
    }

    public double getCurrentMin() {
        return this.currentMin;
    }

    public double getCurrentMax() {
        return this.currentMax;
    }

    public double getDefaultMin() {
        return this.defaultMin;
    }

    public double getDefaultMax() {
        return this.defaultMax;
    }

    public double getStep() {
        return this.step;
    }

    public double getRandomDoubleInRange() {
        if (this.getCurrentMax() > this.getCurrentMin()) {
            return new Random().nextDouble(this.getCurrentMin(), this.getCurrentMax());
        }
        return this.getCurrentMin();
    }

    public int getRandomIntInRange() {
        if (this.getCurrentMax() > this.getCurrentMin()) {
            return new Random().nextInt(this.getMinInt(), this.getMaxInt());
        }
        return this.getMinInt();
    }

    public float getRandomFloatInRange() {
        if (this.getCurrentMax() > this.getCurrentMin()) {
            return new Random().nextFloat(this.getMinFloat(), this.getMaxFloat());
        }
        return this.getMinFloat();
    }

    public long getRandomLongInRange() {
        if (this.getCurrentMax() > this.getCurrentMin()) {
            return new Random().nextLong(this.getMinLong(), this.getMaxLong());
        }
        return this.getMinLong();
    }

    public void setCurrentMin(final double value) {
        final double n = 1.0 / this.step;
        this.currentMin = Math.round(Math.max(this.min, Math.min(this.max, value)) * n) / n;
    }

    public void setCurrentMax(final double value) {
        final double n = 1.0 / this.step;
        this.currentMax = Math.round(Math.max(this.min, Math.min(this.max, value)) * n) / n;
    }
}