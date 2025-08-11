package com.h1ggsk.radon.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import com.h1ggsk.radon.gui.Component;
import com.h1ggsk.radon.module.setting.Setting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.ColorUtil;
import com.h1ggsk.radon.utils.Utils;
import com.h1ggsk.radon.utils.MathUtil;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;

public final class NumberBox extends Component {
    public boolean dragging;
    public double offsetX;
    public double lerpedOffsetX;
    private float hoverAnimation;
    private final NumberSetting setting;
    public Color currentColor1;
    private Color currentAlpha;
    private final Color TEXT_COLOR;
    private final Color HOVER_COLOR;
    private final Color TRACK_BG_COLOR;
    private final float TRACK_HEIGHT = 4.0f;
    private final float TRACK_RADIUS = 2.0f;
    private final float ANIMATION_SPEED = 0.25f;

    public NumberBox(final ModuleButton moduleButton, final Setting setting, final int n) {
        super(moduleButton, setting, n);
        this.lerpedOffsetX = 0.0;
        this.hoverAnimation = 0.0f;
        this.TEXT_COLOR = new Color(230, 230, 230);
        this.HOVER_COLOR = new Color(255, 255, 255, 20);
        this.TRACK_BG_COLOR = new Color(60, 60, 65);
        this.setting = (NumberSetting) setting;
    }

    @Override
    public void onUpdate() {
        final Color mainColor = Utils.getMainColor(255, this.parent.settings.indexOf(this));
        if (this.currentColor1 == null) {
            this.currentColor1 = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 0);
        } else {
            this.currentColor1 = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), this.currentColor1.getAlpha());
        }
        if (this.currentColor1.getAlpha() != 255) {
            this.currentColor1 = ColorUtil.a(0.05f, 255, this.currentColor1);
        }
        super.onUpdate();
    }

    @Override
    public void render(final DrawContext drawContext, final int n, final int n2, final float n3) {
        super.render(drawContext, n, n2, n3);
        this.updateAnimations(n, n2, n3);
        this.offsetX = (this.setting.getValue() - this.setting.getMin()) / (this.setting.getMax() - this.setting.getMin()) * this.parentWidth();
        this.lerpedOffsetX = MathUtil.approachValue((float) (0.5 * n3), this.lerpedOffsetX, this.offsetX);
        if (!this.parent.parent.dragging) {
            drawContext.fill(this.parentX(), this.parentY() + this.parentOffset() + this.offset, this.parentX() + this.parentWidth(), this.parentY() + this.parentOffset() + this.offset + this.parentHeight(), new Color(this.HOVER_COLOR.getRed(), this.HOVER_COLOR.getGreen(), this.HOVER_COLOR.getBlue(), (int) (this.HOVER_COLOR.getAlpha() * this.hoverAnimation)).getRGB());
        }
        final int n4 = this.parentY() + this.offset + this.parentOffset() + 25;
        final int n5 = this.parentX() + 5;
        RenderUtils.renderRoundedQuad(drawContext.getMatrices(), this.TRACK_BG_COLOR, n5, n4, n5 + (this.parentWidth() - 10), n4 + 4.0f, 2.0, 2.0, 2.0, 2.0, 50.0);
        if (this.lerpedOffsetX > 2.5) {
            RenderUtils.renderRoundedQuad(drawContext.getMatrices(), this.currentColor1, n5, n4, n5 + Math.max(this.lerpedOffsetX - 5.0, 0.0), n4 + 4.0f, 2.0, 2.0, 2.0, 2.0, 50.0);
        }
        final String displayValue = this.getDisplayValue();
        TextRenderer.drawString(this.setting.getName(), drawContext, this.parentX() + 5, this.parentY() + this.parentOffset() + this.offset + 9, this.TEXT_COLOR.getRGB());
        TextRenderer.drawString(displayValue, drawContext, this.parentX() + this.parentWidth() - TextRenderer.getWidth(displayValue) - 5, this.parentY() + this.parentOffset() + this.offset + 9, this.currentColor1.getRGB());
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

    private String getDisplayValue() {
        final double a = this.setting.getValue();
        final double c = this.setting.getFormat();
        if (c == 0.1) {
            return String.format("%.1f", a);
        }
        if (c == 0.01) {
            return String.format("%.2f", a);
        }
        if (c == 0.001) {
            return String.format("%.3f", a);
        }
        if (c == 1.0E-4) {
            return String.format("%.4f", a);
        }
        if (c >= 1.0) {
            return String.format("%.0f", a);
        }
        return String.valueOf(a);
    }

    @Override
    public void onGuiClose() {
        this.currentColor1 = null;
        this.hoverAnimation = 0.0f;
        super.onGuiClose();
    }

    private void slide(final double n) {
        this.setting.getValue(MathUtil.roundToNearest(MathHelper.clamp((n - (this.parentX() + 5)) / (this.parentWidth() - 10), 0.0, 1.0) * (this.setting.getMax() - this.setting.getMin()) + this.setting.getMin(), this.setting.getFormat()));
    }

    @Override
    public void keyPressed(final int n, final int n2, final int n3) {
        if (this.mouseOver && this.parent.extended && n == 259) {
            this.setting.getValue(this.setting.getDefaultValue());
        }
        super.keyPressed(n, n2, n3);
    }

    @Override
    public void mouseClicked(final double n, final double n2, final int n3) {
        if (this.isHovered(n, n2) && n3 == 0) {
            this.dragging = true;
            this.slide(n);
        }
        super.mouseClicked(n, n2, n3);
    }

    @Override
    public void mouseReleased(final double n, final double n2, final int n3) {
        if (this.dragging && n3 == 0) {
            this.dragging = false;
        }
        super.mouseReleased(n, n2, n3);
    }

    @Override
    public void mouseDragged(final double n, final double n2, final int n3, final double n4, final double n5) {
        if (this.dragging) {
            this.slide(n);
        }
        super.mouseDragged(n, n2, n3, n4, n5);
    }
}
