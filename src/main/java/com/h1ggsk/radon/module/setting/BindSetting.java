package com.h1ggsk.radon.module.setting;

public final class BindSetting extends Setting {
    private final boolean moduleKey;
    private final int defaultValue;
    private boolean listening;
    private int value;

    public BindSetting(final CharSequence name, final int value, final boolean isModule) {
        super(name);
        this.value = value;
        this.defaultValue = value;
        this.moduleKey = isModule;
    }

    public boolean isModuleKey() {
        return this.moduleKey;
    }

    public boolean isListening() {
        return this.listening;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public void setListening(final boolean listening) {
        this.listening = listening;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(final int a) {
        this.value = a;
    }

    public void toggleListening() {
        this.listening = !this.listening;
    }

    public BindSetting setDescription(final CharSequence charSequence) {
        super.setDescription(charSequence);
        return this;
    }
}