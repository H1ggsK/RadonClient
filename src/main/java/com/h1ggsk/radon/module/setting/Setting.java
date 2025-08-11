package com.h1ggsk.radon.module.setting;

public abstract class Setting {
    private CharSequence name;
    public CharSequence description;

    public Setting(final CharSequence a) {
        this.name = a;
    }

    public void getDescription(final CharSequence a) {
        this.name = a;
    }

    public CharSequence getName() {
        return this.name;
    }

    public CharSequence getDescription() {
        return this.description;
    }

    public Setting setDescription(final CharSequence description) {
        this.description = description;
        return this;
    }
}