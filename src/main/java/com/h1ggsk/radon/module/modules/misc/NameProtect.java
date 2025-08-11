package com.h1ggsk.radon.module.modules.misc;

import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.StringSetting;
import com.h1ggsk.radon.utils.EncryptedString;

public class NameProtect extends Module {
    private final StringSetting fakeName = new StringSetting("Fake Name", "Player");

    public NameProtect() {
        super(EncryptedString.of("Name Protect"), EncryptedString.of("Replaces your name with given one."), -1, Category.MISC);
        this.addSettings(this.fakeName);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public String getFakeName() {
        return this.fakeName.getValue();
    }
}
