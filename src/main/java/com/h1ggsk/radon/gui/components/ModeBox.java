package com.h1ggsk.radon.gui.components;

import net.minecraft.client.gui.DrawContext;
import com.h1ggsk.radon.gui.Component;
import com.h1ggsk.radon.module.setting.Setting;
import com.h1ggsk.radon.module.setting.ModeSetting;
import com.h1ggsk.radon.utils.ColorUtil;
import com.h1ggsk.radon.utils.Utils;
import com.h1ggsk.radon.utils.MathUtil;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;

public final class ModeBox extends Component {
    private final ModeSetting<?> setting;
    private float hoverAnimation;
    private float selectAnimation;
    private float previousSelectAnimation;
    private boolean wasClicked;
    public Color currentColor;
    private final Color TEXT_COLOR;
    private final Color HOVER_COLOR;
    private final Color SELECTOR_BG;
    private final float SELECTOR_HEIGHT = 4.0f;
    private final float SELECTOR_RADIUS = 2.0f;
    private final float HOVER_ANIMATION_SPEED = 0.25f;
    private final float SELECT_ANIMATION_SPEED = 0.15f;

    public ModeBox(final ModuleButton moduleButton, final Setting setting, final int n) {
        super(moduleButton, setting, n);
        this.hoverAnimation = 0.0f;
        this.selectAnimation = 0.0f;
        this.previousSelectAnimation = 0.0f;
        this.wasClicked = false;
        this.TEXT_COLOR = new Color(230, 230, 230);
        this.HOVER_COLOR = new Color(255, 255, 255, 20);
        this.SELECTOR_BG = new Color(40, 40, 45);
        this.setting = (ModeSetting) setting;
    }

    @Override
    public void onUpdate() {
        final Color mainColor = Utils.getMainColor(255, this.parent.settings.indexOf(this));
        if (this.currentColor == null) {
            this.currentColor = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 0);
        } else {
            this.currentColor = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), this.currentColor.getAlpha());
        }
        if (this.currentColor.getAlpha() != 255) {
            this.currentColor = ColorUtil.a(0.05f, 255, this.currentColor);
        }
        super.onUpdate();
    }

    @Override
    public void render(final DrawContext drawContext, final int n, final int n2, final float n3) {
        super.render(drawContext, n, n2, n3);
        this.updateAnimations(n, n2, n3);
        final int index = this.setting.getPossibleValues().indexOf(this.setting.getValue());
        final int size = this.setting.getPossibleValues().size();
        this.parentWidth();
        this.parentX();
        if (!this.parent.parent.dragging) {
            drawContext.fill(this.parentX(), this.parentY() + this.parentOffset() + this.offset, this.parentX() + this.parentWidth(), this.parentY() + this.parentOffset() + this.offset + this.parentHeight(), new Color(this.HOVER_COLOR.getRed(), this.HOVER_COLOR.getGreen(), this.HOVER_COLOR.getBlue(), (int) (this.HOVER_COLOR.getAlpha() * this.hoverAnimation)).getRGB());
        }
        TextRenderer.drawString(String.valueOf(this.setting.getName()), drawContext, this.parentX() + 5, this.parentY() + this.parentOffset() + this.offset + 9, this.TEXT_COLOR.getRGB());
        TextRenderer.drawString(this.setting.getValue().name(), drawContext, this.parentX() + TextRenderer.getWidth(this.setting.getName() + ": ") + 8, this.parentY() + this.parentOffset() + this.offset + 9, this.currentColor.getRGB());
        final int n4 = this.parentY() + this.offset + this.parentOffset() + 25;
        final int n5 = this.parentX() + 5;
        final int n6 = this.parentWidth() - 10;
        RenderUtils.renderRoundedQuad(drawContext, this.SELECTOR_BG, n5, n4, n5 + n6, n4 + 4.0f, 2.0, 2.0, 2.0, 2.0, 50.0);
        final int n7 = index - 1;
        float n8 = (float) n7;
        if (n7 < 0.0f) {
            n8 = (float) (size - 1);
        }
        final int n9 = index + 1;
        float n10 = (float) n9;
        if (n9 >= (float) size) {
            n10 = 0.0f;
        }
        final int n11 = n6 / size;
        float n12;
        if (this.previousSelectAnimation > 0.01f) {
            n12 = (float) MathUtil.linearInterpolate(n5 + n8 * n11, n5 + index * (float) n11, 1.0f - this.previousSelectAnimation);
        } else if (this.selectAnimation > 0.01f) {
            n12 = (float) MathUtil.linearInterpolate(n5 + index * (float) n11, n5 + n10 * n11, this.selectAnimation);
        } else {
            n12 = n5 + index * (float) n11;
        }
        RenderUtils.renderRoundedQuad(drawContext, this.currentColor, n12, n4, n12 + n11, n4 + 4.0f, 2.0, 2.0, 2.0, 2.0, 50.0);
        final int n13 = this.parentY() + this.parentOffset() + this.offset + 9;
        final int parentX = this.parentX();
        final int parentX2 = this.parentX();
        final int parentWidth = this.parentWidth();
        TextRenderer.drawString("\u25c4", drawContext, parentX + this.parentWidth() - 25, n13, this.TEXT_COLOR.getRGB());
        TextRenderer.drawString("\u25ba", drawContext, parentX2 + parentWidth - 12, n13, this.TEXT_COLOR.getRGB());
        if (this.wasClicked) {
            this.wasClicked = false;
            this.previousSelectAnimation = 0.0f;
            this.selectAnimation = 0.01f;
        }
    }

    private void updateAnimations(final int n, final int n2, final float n3) {
        final float n4 = n3 * 0.05f;
        float n5;
        if (this.isHovered(n, n2) && !this.parent.parent.dragging) {
            n5 = 1.0f;
        } else {
            n5 = 0.0f;
        }
        this.hoverAnimation = (float) MathUtil.exponentialInterpolate(this.hoverAnimation, n5, 0.25, n4);
        if (this.selectAnimation > 0.01f) {
            this.selectAnimation = (float) MathUtil.exponentialInterpolate(this.selectAnimation, 0.0, 0.15000000596046448, n4);
            if (this.selectAnimation < 0.01f) {
                this.previousSelectAnimation = 0.99f;
            }
        }
        if (this.previousSelectAnimation > 0.01f) {
            this.previousSelectAnimation = (float) MathUtil.exponentialInterpolate(this.previousSelectAnimation, 0.0, 0.15000000596046448, n4);
        }
    }

    @Override
    public void keyPressed(final int n, final int n2, final int n3) {
        if (this.mouseOver && this.parent.extended) {
            if (n == 259) {
                this.setting.setModeIndex(this.setting.getOriginalValue());
            } else if (n == 262) {
                this.cycleModeForward();
            } else if (n == 263) {
                this.cycleModeBackward();
            }
        }
        super.keyPressed(n, n2, n3);
    }

    private void cycleModeForward() {
        this.setting.cycleUp();
        this.wasClicked = true;
    }

    private void cycleModeBackward() {
        this.setting.cycleDown();
        this.wasClicked = true;
    }

    @Override
    public void mouseClicked(final double n, final double n2, final int n3) {
        if (this.isHovered(n, n2)) {
            if (n3 == 0) {
                this.cycleModeForward();
            } else if (n3 == 1) {
                this.cycleModeBackward();
            } else if (n3 == 2) {
                this.setting.setModeIndex(this.setting.getOriginalValue());
            }
        }
        super.mouseClicked(n, n2, n3);
    }

    @Override
    public void onGuiClose() {
        this.currentColor = null;
        this.hoverAnimation = 0.0f;
        this.selectAnimation = 0.0f;
        this.previousSelectAnimation = 0.0f;
        super.onGuiClose();
    }
}
