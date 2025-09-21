package com.h1ggsk.radon.utils;

import com.h1ggsk.radon.font.Fonts;
import com.h1ggsk.radon.module.modules.client.Radon;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;

public final class TextRenderer {
    public static void drawString(final CharSequence charSequence, final DrawContext drawContext, final int n, final int n2, final int n3) {
        if (Radon.useCustomFont.getValue()) {
            Fonts.FONT.drawString(drawContext.getMatrices(), charSequence, (float) n, (float) n2, n3);
        } else {
            drawLargeString(charSequence, drawContext, n, n2, n3);
        }
    }

    public static int getWidth(final CharSequence charSequence) {
        if (Radon.useCustomFont.getValue()) {
            return Fonts.FONT.getStringWidth(charSequence);
        }
        return com.h1ggsk.radon.Radon.mc.textRenderer.getWidth(charSequence.toString()) * 2;
    }

    public static void drawCenteredString(final CharSequence charSequence, final DrawContext drawContext, final int n, final int n2, final int n3) {
        if (Radon.useCustomFont.getValue()) {
            Fonts.FONT.drawString(drawContext.getMatrices(), charSequence, (float) (n - Fonts.FONT.getStringWidth(charSequence) / 2), (float) n2, n3);
        } else {
            drawCenteredMinecraftText(charSequence, drawContext, n, n2, n3);
        }
    }

    public static void drawLargeString(final CharSequence charSequence, final DrawContext drawContext, final int n, final int n2, final int n3) {
        final Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.scale(2.0f, 2.0f);
        drawContext.drawText(com.h1ggsk.radon.Radon.mc.textRenderer, charSequence.toString(), n / 2, n2 / 2, n3, false);
        matrices.scale(1.0f, 1.0f);
        matrices.popMatrix();
    }

    public static void drawCenteredMinecraftText(final CharSequence charSequence, final DrawContext drawContext, final int n, final int n2, final int n3) {
        final Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.scale(2.0f, 2.0f);
        drawContext.drawText(com.h1ggsk.radon.Radon.mc.textRenderer, (String) charSequence, n / 2 - com.h1ggsk.radon.Radon.mc.textRenderer.getWidth((String) charSequence) / 2, n2 / 2, n3, false);
        matrices.scale(1.0f, 1.0f);
        matrices.popMatrix();
    }
}
