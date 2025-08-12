package com.h1ggsk.radon.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import com.h1ggsk.radon.gui.Component;
import com.h1ggsk.radon.module.setting.Setting;
import com.h1ggsk.radon.module.setting.BindSetting;
import com.h1ggsk.radon.utils.*;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;

public final class Keybind extends Component {
    private final BindSetting keybind;
    private Color accentColor;
    private Color currentAlpha;
    private float hoverAnimation;
    private float listenAnimation;
    private static final Color TEXT_COLOR;
    private static final Color LISTENING_TEXT_COLOR;
    private static final Color HOVER_COLOR;
    private static final Color BUTTON_BG_COLOR;
    private static final Color BUTTON_ACTIVE_BG_COLOR;
    private static final float BUTTON_RADIUS = 4.0f;
    private static final float ANIMATION_SPEED = 0.25f;
    private static final float LISTEN_ANIMATION_SPEED = 0.35f;
    private static final int BUTTON_MIN_WIDTH = 80;
    private static final int BUTTON_PADDING = 16;

    public Keybind(final ModuleButton moduleButton, final Setting setting, final int n) {
        super(moduleButton, setting, n);
        this.hoverAnimation = 0.0f;
        this.listenAnimation = 0.0f;
        this.keybind = (BindSetting) setting;
    }

    @Override
    public void render(final DrawContext drawContext, final int n, final int n2, final float n3) {
        super.render(drawContext, n, n2, n3);
        this.updateAnimations(n, n2, n3);
        if (!this.parent.parent.dragging) {
            drawContext.fill(this.parentX(), this.parentY() + this.parentOffset() + this.offset, this.parentX() + this.parentWidth(), this.parentY() + this.parentOffset() + this.offset + this.parentHeight(), new Color(Keybind.HOVER_COLOR.getRed(), Keybind.HOVER_COLOR.getGreen(), Keybind.HOVER_COLOR.getBlue(), (int) (Keybind.HOVER_COLOR.getAlpha() * this.hoverAnimation)).getRGB());
        }
        TextRenderer.drawString(this.setting.getName(), drawContext, this.parentX() + 5, this.parentY() + this.parentOffset() + this.offset + 9, Keybind.TEXT_COLOR.getRGB());
        String string;
        if (this.keybind.isListening()) {
            string = "Listening...";
        } else {
            string = KeyUtils.getKey(this.keybind.getValue()).toString();
        }
        final int a = TextRenderer.getWidth(string);
        final int max = Math.max(80, a + 16);
        final int n4 = this.parentX() + this.parentWidth() - max - 5;
        final int n5 = this.parentY() + this.parentOffset() + this.offset + (this.parentHeight() - 20) / 2;
        RenderUtils.renderRoundedQuad(drawContext, ColorUtil.a(Keybind.BUTTON_BG_COLOR, Keybind.BUTTON_ACTIVE_BG_COLOR, this.listenAnimation), n4, n5, n4 + max, n5 + 20, 4.0, 4.0, 4.0, 4.0, 50.0);
        final float a2 = this.listenAnimation * 0.7f;
        float b;
        if (this.isButtonHovered(n, n2, n4, n5, max, 20)) {
            b = 0.2f;
        } else {
            b = 0.0f;
        }
        final float max2 = Math.max(a2, b);
        if (max2 > 0.0f) {
            RenderUtils.renderRoundedQuad(drawContext, new Color(this.accentColor.getRed(), this.accentColor.getGreen(), this.accentColor.getBlue(), (int) (this.accentColor.getAlpha() * max2)), n4, n5, n4 + max, n5 + 20, 4.0, 4.0, 4.0, 4.0, 50.0);
        }
        TextRenderer.drawString(string, drawContext, n4 + (max - a) / 2, n5 + 6 - 3, ColorUtil.a(Keybind.TEXT_COLOR, Keybind.LISTENING_TEXT_COLOR, this.listenAnimation).getRGB());
        if (this.keybind.isListening()) {
            RenderUtils.renderRoundedQuad(drawContext, new Color(this.accentColor.getRed(), this.accentColor.getGreen(), this.accentColor.getBlue(), (int) (this.accentColor.getAlpha() * ((float) Math.abs(Math.sin(System.currentTimeMillis() / 500.0)) * 0.3f))), n4, n5, n4 + max, n5 + 20, 4.0, 4.0, 4.0, 4.0, 50.0);
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
        float n6;
        if (this.keybind.isListening()) {
            n6 = 1.0f;
        } else {
            n6 = 0.0f;
        }
        this.listenAnimation = (float) MathUtil.exponentialInterpolate(this.listenAnimation, n6, 0.3499999940395355, n4);
    }

    private boolean isButtonHovered(final double n, final double n2, final int n3, final int n4, final int n5, final int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    @Override
    public void mouseClicked(final double n, final double n2, final int n3) {
        String string;
        if (this.keybind.isListening()) {
            string = "Listening...";
        } else {
            string = KeyUtils.getKey(this.keybind.getValue()).toString();
        }
        final int max = Math.max(80, TextRenderer.getWidth(string) + 16);
        if (this.isButtonHovered(n, n2, this.parentX() + this.parentWidth() - max - 5, this.parentY() + this.parentOffset() + this.offset + (this.parentHeight() - 20) / 2, max, 20)) {
            if (!this.keybind.isListening()) {
                if (n3 == 0) {
                    this.keybind.toggleListening();
                    this.keybind.setListening(true);
                }
            } else {
                if (this.keybind.isModuleKey()) {
                    this.parent.module.setKeybind(n3);
                }
                this.keybind.setValue(n3);
                this.keybind.setListening(false);
            }
        }
        super.mouseClicked(n, n2, n3);
    }

    @Override
    public void keyPressed(final int n, final int n2, final int n3) {
        if (this.keybind.isListening()) {
            if (n == 256) {
                this.keybind.setListening(false);
            } else if (n == 259) {
                if (this.keybind.isModuleKey()) {
                    this.parent.module.setKeybind(-1);
                }
                this.keybind.setValue(-1);
                this.keybind.setListening(false);
            } else {
                if (this.keybind.isModuleKey()) {
                    this.parent.module.setKeybind(n);
                }
                this.keybind.setValue(n);
                this.keybind.setListening(false);
            }
        }
        super.keyPressed(n, n2, n3);
    }

    @Override
    public void onUpdate() {
        final Color mainColor = Utils.getMainColor(255, this.parent.settings.indexOf(this));
        if (this.accentColor == null) {
            this.accentColor = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 0);
        } else {
            this.accentColor = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), this.accentColor.getAlpha());
        }
        if (this.accentColor.getAlpha() != 255) {
            this.accentColor = ColorUtil.a(0.05f, 255, this.accentColor);
        }
        super.onUpdate();
    }

    @Override
    public void onGuiClose() {
        this.accentColor = null;
        this.hoverAnimation = 0.0f;
        this.listenAnimation = 0.0f;
        super.onGuiClose();
    }

    static {
        TEXT_COLOR = new Color(230, 230, 230);
        LISTENING_TEXT_COLOR = new Color(255, 255, 255);
        HOVER_COLOR = new Color(255, 255, 255, 20);
        BUTTON_BG_COLOR = new Color(60, 60, 65);
        BUTTON_ACTIVE_BG_COLOR = new Color(80, 80, 85);
    }
}
