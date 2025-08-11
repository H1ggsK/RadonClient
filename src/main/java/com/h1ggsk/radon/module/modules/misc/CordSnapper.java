package com.h1ggsk.radon.module.modules.misc;

import com.h1ggsk.radon.utils.embed.DiscordWebhook;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BindSetting;
import com.h1ggsk.radon.module.setting.StringSetting;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.KeyUtils;

import java.util.concurrent.CompletableFuture;

public final class CordSnapper extends Module {
    private final BindSetting activateKey = new BindSetting(EncryptedString.of("Activate Key"), -1, false);
    private final StringSetting webhookUrl = new StringSetting(EncryptedString.of("Webhook"), "");
    private int cooldownCounter;

    public CordSnapper() {
        super(EncryptedString.of("Cord Snapper"), EncryptedString.of("Sends base coordinates to Discord webhook"), -1, Category.MISC);
        this.cooldownCounter = 0;
        this.addSettings(this.activateKey, this.webhookUrl);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventListener
    public void onTick(final StartTickEvent event) {
        if (mc.player == null) {
            return;
        }
        if (this.cooldownCounter > 0) {
            --this.cooldownCounter;
            return;
        }
        if (KeyUtils.isKeyPressed(this.activateKey.getValue())) {
            DiscordWebhook embedSender = new DiscordWebhook(this.webhookUrl.value);
            embedSender.a("Coordinates: x: " + mc.player.getX() + " y: " + mc.player.getY() + " z: " + mc.player.getZ());
            CompletableFuture.runAsync(() -> {
                try {
                    embedSender.execute();
                } catch (Throwable _t) {
                    _t.printStackTrace(System.err);
                }
            });
            this.cooldownCounter = 40;
        }
    }
}