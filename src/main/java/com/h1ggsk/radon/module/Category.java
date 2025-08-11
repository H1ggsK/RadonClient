package com.h1ggsk.radon.module;

import com.h1ggsk.radon.utils.EncryptedString;

public enum Category {
    COMBAT(EncryptedString.of("Combat")),
    MOVEMENT(EncryptedString.of("Movement")),
    MISC(EncryptedString.of("Misc")),
    DONUT(EncryptedString.of("Donut")),
    RENDER(EncryptedString.of("Render")),
    CLIENT(EncryptedString.of("Client"));

    public final CharSequence name;

    Category(final CharSequence name) {
        this.name = name;
    }
}
