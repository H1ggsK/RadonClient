package com.h1ggsk.radon.module.setting;

public final class NumberSetting extends Setting {
    private final double min;
    private final double max;
    private double value;
    private final double format;
    private final double defaultValue;

    public NumberSetting(final CharSequence charSequence, final double min, final double max, final double defaultValue, final double format) {
        super(charSequence);
        this.min = min;
        this.max = max;
        this.value = defaultValue;
        this.format = format;
        this.defaultValue = defaultValue;
    }

    public double getValue() {
        return this.value;
    }

    public double getDefaultValue() {
        return this.defaultValue;
    }

    public double getFormat() {
        return this.format;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public int getIntValue() {
        return (int) this.value;
    }

    public float getFloatValue() {
        return (float) this.value;
    }

    public double getDoubleValue() {
        return this.value;
    }

    public long getLongValue() {
        return (long) this.value;
    }

    /**
     * Legacy setter – you can leave this if you have code calling it,
     * but we recommend migrating to setValue(double) below.
     */
    public void getValue(final double b) {
        setValue(b);
    }

    public NumberSetting getValue(final CharSequence charSequence) {
        super.setDescription(charSequence);
        return this;
    }

    /**
     * Set this setting’s value, clamped between [min, max] and rounded
     * to the configured precision (format).
     */
    public void setValue(final double newValue) {
        double step = 1.0 / this.format;
        double clamped = Math.max(this.min, Math.min(this.max, newValue));
        this.value = Math.round(clamped * step) / step;
    }
}
