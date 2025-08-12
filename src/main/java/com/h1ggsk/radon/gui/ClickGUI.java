package com.h1ggsk.radon.gui;

import com.h1ggsk.radon.Radon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.utils.ColorUtil;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ClickGUI extends Screen {
    public List<CategoryWindow> windows;
    public Color currentColor;
    private CharSequence tooltipText;
    private int tooltipX;
    private int tooltipY;
    private final Color DESCRIPTION_BG;

    public ClickGUI() {
        super(Text.empty());
        this.windows = new ArrayList<>();
        this.tooltipText = null;
        this.DESCRIPTION_BG = new Color(40, 40, 40, 200);
        int n = 50;
        final Category[] values = Category.values();
        for (Category value : values) {
            this.windows.add(new CategoryWindow(n, 50, 230, 30, value, this));
            n += 250;
        }
    }

    public boolean isDraggingAlready() {
        for (CategoryWindow window : this.windows) {
            if (window.dragging) {
                return true;
            }
        }
        return false;
    }

    public void setTooltip(final CharSequence tooltipText, final int tooltipX, final int tooltipY) {
        this.tooltipText = tooltipText;
        this.tooltipX = tooltipX;
        this.tooltipY = tooltipY;
    }

    public void setInitialFocus() {
        if (this.client == null) {
            return;
        }
        super.setInitialFocus();
    }

    public void render(final DrawContext drawContext, final int n, final int n2, final float n3) {
        if (Radon.mc.currentScreen == this) {
            if (Radon.INSTANCE.screen != null) {
                Radon.INSTANCE.screen.render(drawContext, 0, 0, n3);
            }
            if (this.currentColor == null) {
                this.currentColor = new Color(0, 0, 0, 0);
            } else {
                this.currentColor = new Color(0, 0, 0, this.currentColor.getAlpha());
            }
            final int alpha = this.currentColor.getAlpha();
            int n4;
            if (com.h1ggsk.radon.module.modules.client.Radon.renderBackground.getValue()) {
                n4 = 200;
            } else {
                n4 = 0;
            }
            if (alpha != n4) {
                int n5;
                if (com.h1ggsk.radon.module.modules.client.Radon.renderBackground.getValue()) {
                    n5 = 200;
                } else {
                    n5 = 0;
                }
                this.currentColor = ColorUtil.a(0.05f, n5, this.currentColor);
            }
            if (Radon.mc.currentScreen instanceof ClickGUI) {
                drawContext.fill(0, 0, Radon.mc.getWindow().getWidth(), Radon.mc.getWindow().getHeight(), this.currentColor.getRGB());
            }
            RenderUtils.unscaledProjection();
            final int n6 = n * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
            final int n7 = n2 * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
            super.render(drawContext, n6, n7, n3);
            for (final CategoryWindow next : this.windows) {
                next.render(drawContext, n6, n7, n3);
                next.updatePosition(n6, n7, n3);
            }
            if (this.tooltipText != null) {
                this.renderTooltip(drawContext, this.tooltipText, this.tooltipX, this.tooltipY);
                this.tooltipText = null;
            }
            RenderUtils.scaledProjection();
        }
    }

    public boolean keyPressed(final int n, final int n2, final int n3) {
        for (CategoryWindow window : this.windows) {
            window.keyPressed(n, n2, n3);
        }
        return super.keyPressed(n, n2, n3);
    }

    public boolean mouseClicked(final double n, final double n2, final int n3) {
        final double n4 = n * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        final double n5 = n2 * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        for (CategoryWindow window : this.windows) {
            window.mouseClicked(n4, n5, n3);
        }
        return super.mouseClicked(n4, n5, n3);
    }

    public boolean mouseDragged(final double n, final double n2, final int n3, final double n4, final double n5) {
        final double n6 = n * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        final double n7 = n2 * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        for (CategoryWindow window : this.windows) {
            window.mouseDragged(n6, n7, n3, n4, n5);
        }
        return super.mouseDragged(n6, n7, n3, n4, n5);
    }

    public boolean mouseScrolled(final double n, final double n2, final double n3, final double n4) {
        final double n5 = n2 * MinecraftClient.getInstance().getWindow().getScaleFactor();
        for (CategoryWindow window : this.windows) {
            window.mouseScrolled(n, n5, n3, n4);
        }
        return super.mouseScrolled(n, n5, n3, n4);
    }

    public boolean shouldPause() {
        return false;
    }

    public void close() {
        Radon.INSTANCE.getModuleManager().getModuleByClass(com.h1ggsk.radon.module.modules.client.Radon.class).setEnabled(false);
        this.onGuiClose();
    }

    public void onGuiClose() {
        Radon.mc.setScreenAndRender(Radon.INSTANCE.screen);
        this.currentColor = null;
        for (CategoryWindow window : this.windows) {
            window.onGuiClose();
        }
    }

    public boolean mouseReleased(final double n, final double n2, final int n3) {
        final double n4 = n * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        final double n5 = n2 * (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        for (CategoryWindow window : this.windows) {
            window.mouseReleased(n4, n5, n3);
        }
        return super.mouseReleased(n4, n5, n3);
    }

    private void renderTooltip(final DrawContext drawContext, final CharSequence charSequence, int n, final int n2) {
        if (charSequence == null || charSequence.isEmpty()) {
            return;
        }
        final int a = TextRenderer.getWidth(charSequence);
        final int framebufferWidth = Radon.mc.getWindow().getFramebufferWidth();
        if (n + a + 10 > framebufferWidth) {
            n = framebufferWidth - a - 10;
        }
        RenderUtils.renderRoundedQuad(drawContext, this.DESCRIPTION_BG, n - 5, n2 - 5, n + a + 5, n2 + 15, 6.0, 6.0, 6.0, 6.0, 50.0);
        TextRenderer.drawString(charSequence, drawContext, n, n2, Color.WHITE.getRGB());
    }

    private static byte[] brdaposwnczucua() {
        return new byte[]{98, 92, 52, 27, 7, 88, 41, 125, 66, 65, 37, 99, 61, 68, 5, 26, 118, 48, 126, 26, 64, 42, 90, 37, 54, 61, 36, 48, 100, 73, 66, 17, 73, 98, 88, 39, 108, 21, 71, 5, 112, 15, 123, 80, 65, 78, 19, 40, 10, 96, 118, 55, 22, 6, 49, 97, 118, 86, 110, 127, 112, 126, 98, 10, 60, 94, 107, 36, 104, 70, 62, 71, 11, 77, 120, 62, 26, 68, 118, 87, 45, 21, 21};
    }
}
