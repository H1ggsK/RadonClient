package com.h1ggsk.radon.gui.components;

import net.minecraft.client.gui.DrawContext;
import com.h1ggsk.radon.gui.Component;
import com.h1ggsk.radon.module.setting.Setting;
import com.h1ggsk.radon.module.setting.StringSetting;
import com.h1ggsk.radon.utils.ColorUtil;
import com.h1ggsk.radon.utils.Utils;
import com.h1ggsk.radon.utils.MathUtil;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;

public final class TextBox extends Component {
    private final StringSetting setting;
    private float hoverAnimation;
    private Color currentColor;
    private final Color TEXT_COLOR;
    private final Color VALUE_COLOR;
    private final Color HOVER_COLOR;
    private final Color INPUT_BG;
    private final Color INPUT_BORDER;
    private final float CORNER_RADIUS = 4.0f;
    private final float HOVER_ANIMATION_SPEED = 0.25f;
    private final int MAX_VISIBLE_CHARS = 7;

    public TextBox(final ModuleButton moduleButton, final Setting setting, final int n) {
        super(moduleButton, setting, n);
        this.hoverAnimation = 0.0f;
        this.TEXT_COLOR = new Color(230, 230, 230);
        this.VALUE_COLOR = new Color(120, 210, 255);
        this.HOVER_COLOR = new Color(255, 255, 255, 20);
        this.INPUT_BG = new Color(30, 30, 35);
        this.INPUT_BORDER = new Color(60, 60, 65);
        this.setting = (StringSetting) setting;
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
        if (!this.parent.parent.dragging) {
            drawContext.fill(this.parentX(), this.parentY() + this.parentOffset() + this.offset, this.parentX() + this.parentWidth(), this.parentY() + this.parentOffset() + this.offset + this.parentHeight(), new Color(this.HOVER_COLOR.getRed(), this.HOVER_COLOR.getGreen(), this.HOVER_COLOR.getBlue(), (int) (this.HOVER_COLOR.getAlpha() * this.hoverAnimation)).getRGB());
        }
        final int n4 = this.parentX() + 5;
        final int n5 = this.parentY() + this.parentOffset() + this.offset + 9;
        TextRenderer.drawString(String.valueOf(this.setting.getName()), drawContext, n4, n5, this.TEXT_COLOR.getRGB());
        final int n6 = n4 + TextRenderer.getWidth(this.setting.getName() + ": ") + 5;
        final int n7 = this.parentWidth() - n6 + this.parentX() - 5;
        final int n8 = n5 - 2;
        RenderUtils.renderRoundedQuad(drawContext, this.INPUT_BORDER, n6, n8, n6 + n7, n8 + 18, 4.0, 4.0, 4.0, 4.0, 50.0);
        RenderUtils.renderRoundedQuad(drawContext, this.INPUT_BG, n6 + 1, n8 + 1, n6 + n7 - 1, n8 + 18 - 1, 3.5, 3.5, 3.5, 3.5, 50.0);
        TextRenderer.drawString(this.formatDisplayValue(this.setting.getValue()), drawContext, n6 + 4, n8 + 3, this.VALUE_COLOR.getRGB());
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

    private String formatDisplayValue(final String s) {
        if (s.isEmpty()) {
            return "...";
        }
        if (s.length() <= 7) {
            return s;
        }
        return s.substring(0, 4) + "...";
    }

    @Override
    public void mouseClicked(final double n, final double n2, final int n3) {
        if (this.isHovered(n, n2) && n3 == 0) {
            this.mc.setScreen(new StringBox(this, this.setting));
        }
        super.mouseClicked(n, n2, n3);
    }

    @Override
    public void onGuiClose() {
        this.currentColor = null;
        this.hoverAnimation = 0.0f;
        super.onGuiClose();
    }
}
