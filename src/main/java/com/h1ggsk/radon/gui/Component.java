package com.h1ggsk.radon.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import com.h1ggsk.radon.gui.components.ModuleButton;
import com.h1ggsk.radon.module.setting.Setting;
import com.h1ggsk.radon.utils.ColorUtil;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;

public abstract class Component {
    public MinecraftClient mc;
    public ModuleButton parent;
    public Setting setting;
    public int offset;
    public Color currentColor;
    public boolean mouseOver;
    int x;
    int y;
    int width;
    int height;

    public Component(final ModuleButton parent, final Setting setting, final int offset) {
        this.mc = MinecraftClient.getInstance();
        this.parent = parent;
        this.setting = setting;
        this.offset = offset;
        this.x = this.parentX();
        this.y = this.parentY() + this.parentOffset() + offset;
        this.width = this.parentX() + this.parentWidth();
        this.height = this.parentY() + this.parentOffset() + offset + this.parentHeight();
    }

    public int parentX() {
        return this.parent.parent.getX();
    }

    public int parentY() {
        return this.parent.parent.getY();
    }

    public int parentWidth() {
        return this.parent.parent.getWidth();
    }

    public int parentHeight() {
        return this.parent.parent.getHeight();
    }

    public int parentOffset() {
        return this.parent.offset;
    }

    public void render(final DrawContext drawContext, final int n, final int n2, final float n3) {
        this.updateMouseOver(n, n2);
        this.x = this.parentX();
        this.y = this.parentY() + this.parentOffset() + this.offset;
        this.width = this.parentX() + this.parentWidth();
        this.height = this.parentY() + this.parentOffset() + this.offset + this.parentHeight();
        drawContext.fill(this.x, this.y, this.width, this.height, this.currentColor.getRGB());
    }

    private void updateMouseOver(final double n, final double n2) {
        this.mouseOver = this.isHovered(n, n2);
    }

    public void renderDescription(final DrawContext drawContext, final int n, final int n2, final float n3) {
        if (this.isHovered(n, n2) && this.setting.getDescription() != null && !this.parent.parent.dragging) {
            final CharSequence s = this.setting.getDescription();
            final int a = TextRenderer.getWidth(s);
            final int n4 = this.mc.getWindow().getWidth() / 2 - a / 2;
            RenderUtils.renderRoundedQuad(drawContext, new Color(100, 100, 100, 100), n4 - 5, this.mc.getWindow().getHeight() / 2 + 294, n4 + a + 5, this.mc.getWindow().getHeight() / 2 + 318, 3.0, 10.0);
            TextRenderer.drawString(s, drawContext, n4, this.mc.getWindow().getHeight() / 2 + 300, Color.WHITE.getRGB());
        }
    }

    public void onGuiClose() {
        this.currentColor = null;
    }

    public void keyPressed(final int n, final int n2, final int n3) {
    }

    public boolean isHovered(final double n, final double n2) {
        return n > this.parentX() && n < this.parentX() + this.parentWidth() && n2 > this.offset + this.parentOffset() + this.parentY() && n2 < this.offset + this.parentOffset() + this.parentY() + this.parentHeight();
    }

    public void onUpdate() {
        if (this.currentColor == null) {
            this.currentColor = new Color(0, 0, 0, 0);
        } else {
            this.currentColor = new Color(0, 0, 0, this.currentColor.getAlpha());
        }
        if (this.currentColor.getAlpha() != 120) {
            this.currentColor = ColorUtil.a(0.05f, 120, this.currentColor);
        }
    }

    public void mouseClicked(final double n, final double n2, final int n3) {
    }

    public void mouseReleased(final double n, final double n2, final int n3) {
    }

    public void mouseDragged(final double n, final double n2, final int n3, final double n4, final double n5) {
    }
}
