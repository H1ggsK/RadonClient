package com.h1ggsk.radon.gui.components;

import net.minecraft.client.gui.DrawContext;
import com.h1ggsk.radon.gui.Component;
import com.h1ggsk.radon.module.setting.Setting;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.utils.Utils;
import com.h1ggsk.radon.utils.MathUtil;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;

public final class Checkbox extends Component {
    private final BooleanSetting setting;
    private float hoverAnimation;
    private float enabledAnimation;
    private final float CORNER_RADIUS = 3.0f;
    private final Color TEXT_COLOR;
    private final Color HOVER_COLOR;
    private final Color BOX_BORDER;
    private final Color BOX_BG;
    private final int BOX_SIZE = 13;
    private final float HOVER_ANIMATION_SPEED = 0.005f;
    private final float TOGGLE_ANIMATION_SPEED = 0.002f;

    public Checkbox(final ModuleButton moduleButton, final Setting setting, final int n) {
        super(moduleButton, setting, n);
        this.hoverAnimation = 0.0f;
        this.enabledAnimation = 0.0f;
        this.TEXT_COLOR = new Color(230, 230, 230);
        this.HOVER_COLOR = new Color(255, 255, 255, 20);
        this.BOX_BORDER = new Color(100, 100, 110);
        this.BOX_BG = new Color(40, 40, 45);
        this.setting = (BooleanSetting) setting;
        float enabledAnimation;
        if (this.setting.getValue()) {
            enabledAnimation = 1.0f;
        } else {
            enabledAnimation = 0.0f;
        }
        this.enabledAnimation = enabledAnimation;
    }

    @Override
    public void render(final DrawContext drawContext, final int n, final int n2, final float n3) {
        super.render(drawContext, n, n2, n3);
        this.updateAnimations(n, n2, n3);
        if (!this.parent.parent.dragging) {
            drawContext.fill(this.parentX(), this.parentY() + this.parentOffset() + this.offset, this.parentX() + this.parentWidth(), this.parentY() + this.parentOffset() + this.offset + this.parentHeight(), new Color(this.HOVER_COLOR.getRed(), this.HOVER_COLOR.getGreen(), this.HOVER_COLOR.getBlue(), (int) (this.HOVER_COLOR.getAlpha() * this.hoverAnimation)).getRGB());
        }
        TextRenderer.drawString(this.setting.getName(), drawContext, this.parentX() + 27, this.parentY() + this.parentOffset() + this.offset + this.parentHeight() / 2 - 6, this.TEXT_COLOR.getRGB());
        this.renderModernCheckbox(drawContext);
    }

    private void updateAnimations(final int n, final int n2, final float n3) {
        final float n4 = n3 * 0.05f;
        float n5;
        if (this.isHovered(n, n2) && !this.parent.parent.dragging) {
            n5 = 1.0f;
        } else {
            n5 = 0.0f;
        }
        this.hoverAnimation = (float) MathUtil.exponentialInterpolate(this.hoverAnimation, n5, 0.004999999888241291, n4);
        float n6;
        if (this.setting.getValue()) {
            n6 = 1.0f;
        } else {
            n6 = 0.0f;
        }
        this.enabledAnimation = (float) MathUtil.exponentialInterpolate(this.enabledAnimation, n6, 0.0020000000949949026, n4);
        this.enabledAnimation = (float) MathUtil.clampValue(this.enabledAnimation, 0.0, 1.0);
    }

    private void renderModernCheckbox(final DrawContext drawContext) {
        final int n = this.parentX() + 8;
        final int n2 = this.parentY() + this.parentOffset() + this.offset + this.parentHeight() / 2 - 6;
        final Color mainColor = Utils.getMainColor(255, this.parent.settings.indexOf(this));
        RenderUtils.renderRoundedQuad(drawContext, this.BOX_BORDER, n, n2, n + 13, n2 + 13, 3.0, 3.0, 3.0, 3.0, 50.0);
        RenderUtils.renderRoundedQuad(drawContext, this.BOX_BG, n + 1, n2 + 1, n + 13 - 1, n2 + 13 - 1, 2.5, 2.5, 2.5, 2.5, 50.0);
        if (this.enabledAnimation > 0.01f) {
            final Color color = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), (int) (255.0f * this.enabledAnimation));
            final float n3 = n + 2 + 9.0f * (1.0f - this.enabledAnimation) / 2.0f;
            final float n4 = n2 + 2 + 9.0f * (1.0f - this.enabledAnimation) / 2.0f;
            RenderUtils.renderRoundedQuad(drawContext, color, n3, n4, n3 + 9.0f * this.enabledAnimation, n4 + 9.0f * this.enabledAnimation, 1.5, 1.5, 1.5, 1.5, 50.0);
            if (this.enabledAnimation > 0.7f) {
                RenderUtils.renderRoundedQuad(drawContext, new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), (int) (40.0f * ((this.enabledAnimation - 0.7f) * 3.33f))), n - 1, n2 - 1, n + 13 + 1, n2 + 13 + 1, 3.5, 3.5, 3.5, 3.5, 50.0);
            }
        }
    }

    @Override
    public void keyPressed(final int n, final int n2, final int n3) {
        if (this.mouseOver && this.parent.extended && n == 259) {
            this.setting.setValue(this.setting.getDefaultValue());
        }
        super.keyPressed(n, n2, n3);
    }

    @Override
    public void mouseClicked(final double n, final double n2, final int n3) {
        if (this.isHovered(n, n2) && n3 == 0) {
            this.setting.toggle();
        }
        super.mouseClicked(n, n2, n3);
    }

    @Override
    public void onGuiClose() {
        super.onGuiClose();
        this.hoverAnimation = 0.0f;
        float enabledAnimation;
        if (this.setting.getValue()) {
            enabledAnimation = 1.0f;
        } else {
            enabledAnimation = 0.0f;
        }
        this.enabledAnimation = enabledAnimation;
    }
}
