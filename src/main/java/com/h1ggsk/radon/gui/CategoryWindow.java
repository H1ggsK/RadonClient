package com.h1ggsk.radon.gui;

import com.h1ggsk.radon.Radon;
import net.minecraft.client.gui.DrawContext;
import com.h1ggsk.radon.gui.components.ModuleButton;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.utils.*;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class CategoryWindow {
    public List<ModuleButton> moduleButtons;
    public int x;
    public int y;
    private final int width;
    private final int height;
    public Color currentColor;
    private final Category category;
    public boolean dragging;
    public boolean extended;
    private int dragX;
    private int dragY;
    private int prevX;
    private int prevY;
    public ClickGUI parent;
    private float hoverAnimation;

    public CategoryWindow(final int x, final int y, final int width, final int height, final Category category, final ClickGUI parent) {
        this.moduleButtons = new ArrayList<>();
        this.hoverAnimation = 0.0f;
        this.x = x;
        this.y = y;
        this.width = width;
        this.dragging = false;
        this.extended = true;
        this.height = height;
        this.category = category;
        this.parent = parent;
        this.prevX = x;
        this.prevY = y;

        final List<Module> modules = new ArrayList<>(Radon.INSTANCE.getModuleManager().getModulesFromCategory(category));
        int offset = height;

        for (Module module : modules) {
            this.moduleButtons.add(new ModuleButton(this, module, offset));
            offset += height;
        }
    }

    public void render(final DrawContext context, final int n, final int n2, final float n3) {
        final Color color = new Color(25, 25, 30, com.h1ggsk.radon.module.modules.client.Radon.windowAlpha.getIntValue());
        if (this.currentColor == null) {
            this.currentColor = new Color(25, 25, 30, 0);
        } else {
            this.currentColor = ColorUtil.a(0.05f, color, this.currentColor);
        }
        float n4 = this.isHovered(n, n2) && !this.dragging ? 1.0F : 0.0F;
        this.hoverAnimation = (float) MathUtil.approachValue(n3 * 0.1f, this.hoverAnimation, n4);
        final Color a = ColorUtil.a(new Color(25, 25, 30, this.currentColor.getAlpha()), new Color(255, 255, 255, 20), this.hoverAnimation);
        float n5 = this.extended ? 0.0F : 6.0F;
        float n6 = this.extended ? 0.0F : 6.0F;
        RenderUtils.renderRoundedQuad(context, a, this.prevX, this.prevY, this.prevX + this.width, this.prevY + this.height, 6.0, 6.0, n5, n6, 50.0);
        final Color mainColor = Utils.getMainColor(255, this.category.ordinal());
        final CharSequence f = this.category.name;
        final int n7 = this.prevX + (this.width - TextRenderer.getWidth(this.category.name)) / 2;
        final int n8 = this.prevY + 8;
        TextRenderer.drawString(f, context, n7 + 1, n8 + 1, new Color(0, 0, 0, 100).getRGB());
        TextRenderer.drawString(f, context, n7, n8, mainColor.brighter().getRGB());
        this.updateButtons(n3);
        if (this.extended) {
            this.renderModuleButtons(context, n, n2, n3);
        }
    }

    private void renderModuleButtons(final DrawContext context, final int n, final int n2, final float n3) {
        for (ModuleButton module : this.moduleButtons) {
            module.render(context, n, n2, n3);
        }
    }

    public void keyPressed(final int n, final int n2, final int n3) {
        for (ModuleButton moduleButton : this.moduleButtons) {
            moduleButton.keyPressed(n, n2, n3);
        }
    }

    public void onGuiClose() {
        this.currentColor = null;
        for (ModuleButton moduleButton : this.moduleButtons) {
            moduleButton.onGuiClose();
        }
        this.dragging = false;
    }

    public void mouseClicked(final double x, final double y, final int button) {
        if (this.isHovered(x, y)) {
            // Calculate keybindListener unique identifier based on the mouse button input
            switch (button) {
                case 0: // Case for left mouse button
                    if (!this.parent.isDraggingAlready()) {
                        this.dragging = true;
                        this.dragX = (int) (x - this.x);
                        this.dragY = (int) (y - this.y);
                    }
                    break;

                case 1: // Case for right mouse button
                    // Add meaningful logic here if needed
                    break;

                default:
                    // Handle unexpected cases (optional)
                    break;
            }
        }
        if (this.extended) {
            for (ModuleButton moduleButton : this.moduleButtons) {
                moduleButton.mouseClicked(x, y, button);
            }
        }
    }

    public void mouseDragged(final double n, final double n2, final int n3, final double n4, final double n5) {
        if (this.extended) {
            for (ModuleButton moduleButton : this.moduleButtons) {
                moduleButton.mouseDragged(n, n2, n3, n4, n5);
            }
        }
    }

    public void updateButtons(final float n) {
        int height = this.height;
        for (final ModuleButton next : this.moduleButtons) {
            final Animation animation = next.animation;
            double n2;
            if (next.extended) {
                n2 = this.height * (next.settings.size() + 1);
            } else {
                n2 = this.height;
            }
            animation.animate(0.5 * n, n2);
            final double animation2 = next.animation.getAnimation();
            next.offset = height;

            height += (int) animation2;
        }
    }

    public void mouseReleased(final double n, final double n2, final int n3) {
        if (n3 == 0 && this.dragging) {
            this.dragging = false;
        }
        for (ModuleButton moduleButton : this.moduleButtons) {
            moduleButton.mouseReleased(n, n2, n3);
        }
    }

    public void mouseScrolled(final double n, final double n2, final double n3, final double n4) {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevY += (int) (n4 * 20.0);
        this.setY((int) (this.y + n4 * 20.0));
    }

    public int getX() {
        return this.prevX;
    }

    public int getY() {
        return this.prevY;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isHovered(final double n, final double n2) {
        return n > this.x && n < this.x + this.width && n2 > this.y && n2 < this.y + this.height;
    }

    public boolean isPrevHovered(final double n, final double n2) {
        return n > this.prevX && n < this.prevX + this.width && n2 > this.prevY && n2 < this.prevY + this.height;
    }

    public void updatePosition(final double n, final double n2, final float n3) {
        this.prevX = this.x;
        this.prevY = this.y;
        if (this.dragging) {
            double n4;
            if (this.isHovered(n, n2)) {
                n4 = this.x;
            } else {
                n4 = this.prevX;
            }
            this.x = (int) MathUtil.approachValue(0.3f * n3, n4, n - this.dragX);
            double n5;
            if (this.isHovered(n, n2)) {
                n5 = this.y;
            } else {
                n5 = this.prevY;
            }
            this.y = (int) MathUtil.approachValue(0.3f * n3, n5, n2 - this.dragY);
        }
    }

    private static byte[] vbfixpesqoeicux() {
        return new byte[]{9, 39, 37, 116, 77, 48, 79, 112, 77, 114, 96, 59, 15, 85, 93, 58, 76, 29, 27, 107, 82, 38, 14, 37, 19, 125, 30, 87, 69, 24, 57, 76, 124, 68, 96, 106, 110, 78, 64, 115, 65, 67, 26, 55, 98, 72, 35, 74, 102, 123, 44, 126, 22, 89, 36, 23, 52, 71, 16, 27, 110, 57, 122, 56, 81, 70, 17, 14, 88, 36, 66, 45, 125, 98, 117, 60, 90, 125, 23, 122, 79, 93, 89, 126, 41, 19, 46, 6, 22, 9, 25};
    }
}
