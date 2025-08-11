package com.h1ggsk.radon.module.setting;

import java.util.Arrays;
import java.util.List;

public final class ModeSetting<T extends Enum<T>> extends Setting {
    public int index;
    private final List<T> possibleValues;
    private final int originalValue;

    public ModeSetting(final CharSequence charSequence, final T defaultValue, final Class<T> type) {
        super(charSequence);
        this.possibleValues = Arrays.asList((T[]) type.getEnumConstants());
        this.index = this.possibleValues.indexOf(defaultValue);
        this.originalValue = this.index;
    }

    public Enum<T> getValue() {
        return this.possibleValues.get(this.index);
    }

    public void setMode(final Enum<T> enum1) {
        this.index = this.possibleValues.indexOf(enum1);
    }

    public void setModeIndex(final int a) {
        this.index = a;
    }

    public int getModeIndex() {
        return this.index;
    }

    public int getOriginalValue() {
        return this.originalValue;
    }

    public void cycleUp() {
        if (this.index < this.possibleValues.size() - 1) {
            ++this.index;
        } else {
            this.index = 0;
        }
    }

    public void cycleDown() {
        if (this.index > 0) {
            --this.index;
        } else {
            this.index = this.possibleValues.size() - 1;
        }
    }

    public boolean isMode(final Enum<T> enum1) {
        return this.index == this.possibleValues.indexOf(enum1);
    }

    public List<T> getPossibleValues() {
        return this.possibleValues;
    }

    public ModeSetting<T> setDescription(final CharSequence charSequence) {
        super.setDescription(charSequence);
        return this;
    }
}
