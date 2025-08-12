package com.h1ggsk.radon.gui.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import com.h1ggsk.radon.gui.Component;
import com.h1ggsk.radon.module.setting.Setting;
import com.h1ggsk.radon.module.setting.ItemSetting;
import com.h1ggsk.radon.utils.ColorUtil;
import com.h1ggsk.radon.utils.Utils;
import com.h1ggsk.radon.utils.MathUtil;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.TextRenderer;

import java.awt.*;

public final class ItemBox extends Component {
    private final ItemSetting setting;
    private float hoverAnimation;
    private Color currentColor;
    private final Color TEXT_COLOR;
    private final Color HOVER_COLOR;
    private final Color ITEM_BG;
    private final Color ITEM_BORDER;
    private final float CORNER_RADIUS = 4.0f;
    private final float HOVER_ANIMATION_SPEED = 0.25f;

    public ItemBox(final ModuleButton moduleButton, final Setting setting, final int n) {
        super(moduleButton, setting, n);
        this.hoverAnimation = 0.0f;
        this.TEXT_COLOR = new Color(230, 230, 230);
        this.HOVER_COLOR = new Color(255, 255, 255, 20);
        this.ITEM_BG = new Color(30, 30, 35);
        this.ITEM_BORDER = new Color(60, 60, 65);
        this.setting = (ItemSetting) setting;
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
        final int n5 = this.parentY() + this.parentOffset() + this.offset + this.parentHeight() / 2;
        TextRenderer.drawString(String.valueOf(this.setting.getName()), drawContext, n4, n5 - 8, this.TEXT_COLOR.getRGB());
        final int n6 = n4 + TextRenderer.getWidth(this.setting.getName() + ": ") + 5;
        final int n7 = n5 - 11;
        RenderUtils.renderRoundedQuad(drawContext, this.ITEM_BORDER, n6, n7, n6 + 22, n7 + 22, 4.0, 4.0, 4.0, 4.0, 50.0);
        RenderUtils.renderRoundedQuad(drawContext, this.ITEM_BG, n6 + 1, n7 + 1, n6 + 22 - 1, n7 + 22 - 1, 3.5, 3.5, 3.5, 3.5, 50.0);
        final Item a = this.setting.getItem();
        if (a != null && a != Items.AIR) {
            drawContext.drawItem(new ItemStack(a), n6 + 3, n7 + 3);
        } else {
            TextRenderer.drawCenteredString("?", drawContext, n6 + 11 - 1, n7 + 4, new Color(150, 150, 150, 200).getRGB());
        }
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

    @Override
    public void mouseClicked(final double n, final double n2, final int n3) {
        if (this.isHovered(n, n2) && n3 == 0) {
            this.mc.setScreen(new ItemFilter(this, this.setting));
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
