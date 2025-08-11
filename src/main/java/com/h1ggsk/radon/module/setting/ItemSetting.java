package com.h1ggsk.radon.module.setting;

import net.minecraft.item.Item;

public class ItemSetting extends Setting {
    private final Item defaultValue;
    private Item value;

    public ItemSetting(final CharSequence name, final Item value) {
        super(name);
        this.value = value;
        this.defaultValue = value;
    }

    public Item getItem() {
        return this.value;
    }

    public void setItem(final Item a) {
        this.value = a;
    }

    public Item getDefaultValue() {
        return this.defaultValue;
    }

    public void resetValue() {
        this.value = this.defaultValue;
    }
}