package com.h1ggsk.radon.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import com.h1ggsk.radon.gui.Component;
import com.h1ggsk.radon.module.setting.Setting;
import com.h1ggsk.radon.module.setting.MinMaxSetting;
import com.h1ggsk.radon.utils.ColorUtil;
import com.h1ggsk.radon.utils.Utils;
import com.h1ggsk.radon.utils.MathUtil;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;

public final class Slider extends Component {
    private boolean draggingMin;
    private boolean draggingMax;
    private double offsetMinX;
    private double offsetMaxX;
    public double lerpedOffsetMinX;
    public double lerpedOffsetMaxX;
    private float hoverAnimation;
    private final MinMaxSetting setting;
    public Color accentColor1;
    public Color accentColor2;
    private static final Color TEXT_COLOR;
    private static final Color HOVER_COLOR;
    private static final Color TRACK_BG_COLOR;
    private static final Color THUMB_COLOR;
    private static final float TRACK_HEIGHT = 4.0f;
    private static final float TRACK_RADIUS = 2.0f;
    private static final float THUMB_SIZE = 8.0f;
    private static final float ANIMATION_SPEED = 0.25f;

    public Slider(final ModuleButton moduleButton, final Setting setting, final int n) {
        super(moduleButton, setting, n);
        this.hoverAnimation = 0.0f;
        this.setting = (MinMaxSetting) setting;
        this.lerpedOffsetMinX = this.parentX();
        this.lerpedOffsetMaxX = this.parentX() + this.parentWidth();
    }

    @Override
    public void render(final DrawContext drawContext, final int n, final int n2, final float n3) {
        super.render(drawContext, n, n2, n3);
        final MatrixStack matrices = drawContext.getMatrices();
        this.updateAnimations(n, n2, n3);
        this.offsetMinX = (this.setting.getCurrentMin() - this.setting.getMinValue()) / (this.setting.getMaxValue() - this.setting.getMinValue()) * (this.parentWidth() - 10) + 5.0;
        this.offsetMaxX = (this.setting.getCurrentMax() - this.setting.getMinValue()) / (this.setting.getMaxValue() - this.setting.getMinValue()) * (this.parentWidth() - 10) + 5.0;
        this.lerpedOffsetMinX = MathUtil.approachValue((float) (0.5 * n3), this.lerpedOffsetMinX, this.offsetMinX);
        this.lerpedOffsetMaxX = MathUtil.approachValue((float) (0.5 * n3), this.lerpedOffsetMaxX, this.offsetMaxX);
        if (!this.parent.parent.dragging) {
            drawContext.fill(this.parentX(), this.parentY() + this.parentOffset() + this.offset, this.parentX() + this.parentWidth(), this.parentY() + this.parentOffset() + this.offset + this.parentHeight(), new Color(Slider.HOVER_COLOR.getRed(), Slider.HOVER_COLOR.getGreen(), Slider.HOVER_COLOR.getBlue(), (int) (Slider.HOVER_COLOR.getAlpha() * this.hoverAnimation)).getRGB());
        }
        final int n4 = this.parentY() + this.offset + this.parentOffset() + 25;
        final int n5 = this.parentX() + 5;
        RenderUtils.renderRoundedQuad(matrices, Slider.TRACK_BG_COLOR, n5, n4, n5 + (this.parentWidth() - 10), n4 + 4.0f, 2.0, 2.0, 2.0, 2.0, 50.0);
        if (this.lerpedOffsetMaxX > this.lerpedOffsetMinX) {
            RenderUtils.renderRoundedQuad(matrices, this.accentColor1, n5 + this.lerpedOffsetMinX - 5.0, n4, n5 + this.lerpedOffsetMaxX - 5.0, n4 + 4.0f, 2.0, 2.0, 2.0, 2.0, 50.0);
        }
        final String displayText = this.getDisplayText();
        TextRenderer.drawString(this.setting.getName(), drawContext, this.parentX() + 5, this.parentY() + this.parentOffset() + this.offset + 9, Slider.TEXT_COLOR.getRGB());
        TextRenderer.drawString(displayText, drawContext, this.parentX() + this.parentWidth() - TextRenderer.getWidth(displayText) - 5, this.parentY() + this.parentOffset() + this.offset + 9, this.accentColor1.getRGB());
        final float n6 = n4 + 2.0f - 4.0f;
        RenderUtils.renderRoundedQuad(matrices, Slider.THUMB_COLOR, (float) (n5 + this.lerpedOffsetMinX - 5.0 - 4.0), n6, (float) (n5 + this.lerpedOffsetMinX - 5.0 + 4.0), n6 + 8.0f, 4.0, 4.0, 4.0, 4.0, 50.0);
        RenderUtils.renderRoundedQuad(matrices, Slider.THUMB_COLOR, (float) (n5 + this.lerpedOffsetMaxX - 5.0 - 4.0), n6, (float) (n5 + this.lerpedOffsetMaxX - 5.0 + 4.0), n6 + 8.0f, 4.0, 4.0, 4.0, 4.0, 50.0);
    }

    private void updateAnimations(final int n, final int n2, final float n3) {
        float n4;
        if (this.isHovered(n, n2) && !this.parent.parent.dragging) {
            n4 = 1.0f;
        } else {
            n4 = 0.0f;
        }
        this.hoverAnimation = (float) MathUtil.exponentialInterpolate(this.hoverAnimation, n4, 0.25, n3 * 0.05f);
    }

    private String getDisplayText() {
        if (this.setting.getCurrentMin() == this.setting.getCurrentMax()) {
            return this.formatValue(this.setting.getCurrentMin());
        }
        return this.formatValue(this.setting.getCurrentMin()) + " - " + this.formatValue(this.setting.getCurrentMax());
    }

    private String formatValue(final double d) {
        final double m = this.setting.getStep();
        if (m == 0.1) {
            return String.format("%.1f", d);
        }
        if (m == 0.01) {
            return String.format("%.2f", d);
        }
        if (m == 0.001) {
            return String.format("%.3f", d);
        }
        if (m >= 1.0) {
            return String.format("%.0f", d);
        }
        return String.valueOf(d);
    }

    @Override
    public void mouseClicked(final double n, final double n2, final int n3) {
        if (n3 == 0 && this.isHovered(n, n2)) {
            if (this.isHoveredMin(n, n2)) {
                this.draggingMin = true;
                this.slideMin(n);
            } else if (this.isHoveredMax(n, n2)) {
                this.draggingMax = true;
                this.slideMax(n);
            } else if (n < this.parentX() + this.offsetMinX) {
                this.draggingMin = true;
                this.slideMin(n);
            } else if (n > this.parentX() + this.offsetMaxX) {
                this.draggingMax = true;
                this.slideMax(n);
            } else if (n - (this.parentX() + this.offsetMinX) < this.parentX() + this.offsetMaxX - n) {
                this.draggingMin = true;
                this.slideMin(n);
            } else {
                this.draggingMax = true;
                this.slideMax(n);
            }
        }
        super.mouseClicked(n, n2, n3);
    }

    @Override
    public void keyPressed(final int n, final int n2, final int n3) {
        if (this.mouseOver && n == 259) {
            this.setting.setCurrentMax(this.setting.getDefaultMax());
            this.setting.setCurrentMin(this.setting.getDefaultMin());
        }
        super.keyPressed(n, n2, n3);
    }

    public boolean isHoveredMin(final double n, final double n2) {
        return this.isHovered(n, n2) && n > this.parentX() + this.offsetMinX - 8.0 && n < this.parentX() + this.offsetMinX + 8.0;
    }

    public boolean isHoveredMax(final double n, final double n2) {
        return this.isHovered(n, n2) && n > this.parentX() + this.offsetMaxX - 8.0 && n < this.parentX() + this.offsetMaxX + 8.0;
    }

    @Override
    public void mouseReleased(final double n, final double n2, final int n3) {
        if (n3 == 0) {
            this.draggingMin = false;
            this.draggingMax = false;
        }
        super.mouseReleased(n, n2, n3);
    }

    @Override
    public void mouseDragged(final double n, final double n2, final int n3, final double n4, final double n5) {
        if (this.draggingMin) {
            this.slideMin(n);
        }
        if (this.draggingMax) {
            this.slideMax(n);
        }
        super.mouseDragged(n, n2, n3, n4, n5);
    }

    @Override
    public void onGuiClose() {
        this.accentColor1 = null;
        this.accentColor2 = null;
        this.hoverAnimation = 0.0f;
        super.onGuiClose();
    }

    private void slideMin(final double n) {
        this.setting.setCurrentMin(Math.min(MathUtil.roundToNearest(MathHelper.clamp((n - (this.parentX() + 5)) / (this.parentWidth() - 10), 0.0, 1.0) * (this.setting.getMaxValue() - this.setting.getMinValue()) + this.setting.getMinValue(), this.setting.getStep()), this.setting.getCurrentMax()));
    }

    private void slideMax(final double n) {
        this.setting.setCurrentMax(Math.max(MathUtil.roundToNearest(MathHelper.clamp((n - (this.parentX() + 5)) / (this.parentWidth() - 10), 0.0, 1.0) * (this.setting.getMaxValue() - this.setting.getMinValue()) + this.setting.getMinValue(), this.setting.getStep()), this.setting.getCurrentMin()));
    }

    @Override
    public void onUpdate() {
        final Color mainColor = Utils.getMainColor(255, this.parent.settings.indexOf(this));
        final Color mainColor2 = Utils.getMainColor(255, this.parent.settings.indexOf(this) + 1);
        if (this.accentColor1 == null) {
            this.accentColor1 = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 0);
        } else {
            this.accentColor1 = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), this.accentColor1.getAlpha());
        }
        if (this.accentColor2 == null) {
            this.accentColor2 = new Color(mainColor2.getRed(), mainColor2.getGreen(), mainColor2.getBlue(), 0);
        } else {
            this.accentColor2 = new Color(mainColor2.getRed(), mainColor2.getGreen(), mainColor2.getBlue(), this.accentColor2.getAlpha());
        }
        if (this.accentColor1.getAlpha() != 255) {
            this.accentColor1 = ColorUtil.a(0.05f, 255, this.accentColor1);
        }
        if (this.accentColor2.getAlpha() != 255) {
            this.accentColor2 = ColorUtil.a(0.05f, 255, this.accentColor2);
        }
        super.onUpdate();
    }

    static {
        TEXT_COLOR = new Color(230, 230, 230);
        HOVER_COLOR = new Color(255, 255, 255, 20);
        TRACK_BG_COLOR = new Color(60, 60, 65);
        THUMB_COLOR = new Color(240, 240, 240);
    }
}
