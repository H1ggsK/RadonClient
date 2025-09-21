package com.h1ggsk.radon.font;

import com.h1ggsk.radon.utils.EncryptedString;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public final class GlyphPageFontRenderer {
    public Random random;
    private float posX;
    private float posY;
    private final int[] colorCode;
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderline;
    private boolean isStrikethrough;
    private final GlyphPage regular;
    private final GlyphPage bold;
    private final GlyphPage italic;
    private final GlyphPage boldItalic;

    public GlyphPageFontRenderer(final GlyphPage regular, final GlyphPage bold, final GlyphPage italic, final GlyphPage boldItalic) {
        this.random = new Random();
        this.colorCode = new int[32];
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        for (int n = 0; n < 32; ++n) {
            int j = (n >> 3 & 0x1) * 85;
            int k = (n >> 2 & 0x1) * 170 + j;
            int l = (n >> 1 & 0x1) * 170 + j;
            int m = (n & 0x1) * 170 + j;

            if (n == 6) k += 85;

            if (n >= 16) {
                k /= 4;
                l /= 4;
                m /= 4;
            }

            this.colorCode[n] = ((k & 0xFF) << 16 | (l & 0xFF) << 8 | (m & 0xFF));
        }
    }

    public static GlyphPageFontRenderer a(final CharSequence font, final int size, final boolean bold, final boolean italic, final boolean boldItalic) {
        final char[] chars = new char[256];
        for (int i = 0; i < 256; ++i) chars[i] = (char) i;
        final GlyphPage regularPage = new GlyphPage(new Font(font.toString(), Font.PLAIN, size), true, true);
        regularPage.generate(chars);
        regularPage.setup();

        GlyphPage boldPage = regularPage;
        GlyphPage italicPage = regularPage;
        GlyphPage boldItalicPage = regularPage;

        if (bold) {
            boldPage = new GlyphPage(new Font(font.toString(), Font.BOLD, size), true, true);
            boldPage.generate(chars);
            boldPage.setup();
        }

        if (italic) {
            italicPage = new GlyphPage(new Font(font.toString(), Font.ITALIC, size), true, true);
            italicPage.generate(chars);
            italicPage.setup();
        }

        if (boldItalic) {
            boldItalicPage = new GlyphPage(new Font(font.toString(), Font.BOLD | Font.ITALIC, size), true, true);
            boldItalicPage.generate(chars);
            boldItalicPage.setup();
        }

        return new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
    }

    public static GlyphPageFontRenderer init(final CharSequence id, final int size, final boolean bold, final boolean italic, final boolean boldItalic) {
        try {
            final char[] chars = new char[256];
            for (int i = 0; i < chars.length; i++) chars[i] = (char) i;

            Font font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(GlyphPageFontRenderer.class.getResourceAsStream(id.toString()))).deriveFont(Font.PLAIN, size);

            GlyphPage regularPage = new GlyphPage(font, true, true);
            regularPage.generate(chars);
            regularPage.setup();

            GlyphPage boldPage = regularPage;
            GlyphPage italicPage = regularPage;
            GlyphPage boldItalicPage = regularPage;

            if (bold) {
                boldPage = new GlyphPage(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(GlyphPageFontRenderer.class.getResourceAsStream(id.toString()))).deriveFont(Font.BOLD, size), true, true);
                boldPage.generate(chars);
                boldPage.setup();
            }

            if (italic) {
                italicPage = new GlyphPage(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(GlyphPageFontRenderer.class.getResourceAsStream(id.toString()))).deriveFont(Font.ITALIC, size), true, true);
                italicPage.generate(chars);
                italicPage.setup();
            }

            if (boldItalic) {
                boldItalicPage = new GlyphPage(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(GlyphPageFontRenderer.class.getResourceAsStream(id.toString()))).deriveFont(Font.BOLD | Font.ITALIC, size), true, true);
                boldItalicPage.generate(chars);
                boldItalicPage.setup();
            }

            return new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
            return null;
        }
    }

    public int drawStringWithShadow(final MatrixStack matrices, final CharSequence text, final float x, final float y, final int color) {
        return this.drawString(matrices, text, x, y, color, true);
    }

    public int drawStringWithShadow(final MatrixStack matrices, final CharSequence text, final double x, final double y, final int color) {
        return this.drawString(matrices, text, (float) x, (float) y, color, true);
    }

    public int drawString(final MatrixStack matrices, final CharSequence text, final float x, final float y, final int color) {
        return this.drawString(matrices, text, x, y, color, false);
    }

    public int drawString(final MatrixStack matrices, final CharSequence text, final double x, final double y, final int color) {
        return this.drawString(matrices, text, (float) x, (float) y, color, false);
    }

    public int drawCenteredString(final MatrixStack matrices, final CharSequence text, final double x, final double y, final float scale, final int color) {
        return this.drawString(matrices, text, (float) x - (float) this.getStringWidth(text) / 2, (float) y, scale, color, false);
    }

    public int drawCenteredString(final MatrixStack matrices, final CharSequence text, final double x, final double y, final int color) {
        return this.drawString(matrices, text, (float) x - (float) this.getStringWidth(text) / 2, (float) y, color, false);
    }

    public int drawCenteredStringWithShadow(final MatrixStack matrices, final CharSequence text, final double x, final double y, final int color) {
        return this.drawString(matrices, text, (float) x - (float) this.getStringWidth(text) / 2, (float) y, color, true);
    }

    public int drawString(final MatrixStack matrices, final CharSequence text, final float x, final float y, final float scale, final int color, final boolean shadow) {
        this.resetStyles();
        return shadow ? Math.max(this.renderString(matrices, text, x + 1.0f, y + 1.0f, scale, color, true), this.renderString(matrices, text, x, y, scale, color, false)) : this.renderString(matrices, text, x, y, scale, color, false);
    }

    public int drawString(final MatrixStack matrices, final CharSequence text, final float x, final float y, final int color, final boolean shadow) {
        this.resetStyles();
        return shadow ? Math.max(this.renderString(matrices, text, x + 1.0f, y + 1.0f, color, true), this.renderString(matrices, text, x, y, color, false)) : this.renderString(matrices, text, x, y, color, false);
    }

    private int renderString(final MatrixStack matrices, final CharSequence text, final float x, final float y, int color, final boolean shadow) {
        if (text == null) return 0;
        if ((color & 0xFC000000) == 0x0) color |= 0xFF000000;
        if (shadow) color = ((color & 0xFCFCFC) >> 2 | (color & 0xFF000000));
        this.posX = x * 2.0f;
        this.posY = y * 2.0f;
        this.a(matrices, text, shadow, color);
        return (int) (this.posX / 4.0f);
    }

    private int renderString(final MatrixStack matrices, final CharSequence text, final float x, final float y, final float scale, int color, final boolean shadow) {
        if (text == null) return 0;
        if ((color & 0xFC000000) == 0x0) color |= 0xFF000000;
        if (shadow) color = ((color & 0xFCFCFC) >> 2 | (color & 0xFF000000));
        this.posX = x * 2.0f;
        this.posY = y * 2.0f;
        this.renderStringAtPos(matrices, text, scale, shadow, color);
        return (int) (this.posX / 4.0f);
    }

    private void a(final MatrixStack matrices, final CharSequence text, final boolean shadow, final int color) {
        GlyphPage page = this.getPage();

        float g = (color >> 16 & 0xFF) / 255.0f;
        float h = (color >> 8 & 0xFF) / 255.0f;
        float k = (color & 0xFF) / 255.0f;

        matrices.push();
        matrices.scale(0.5f, 0.5f, 0.5f);

        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        page.bind();

        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i);

            if (ch == '�' && i + 1 < text.length()) {
                int index = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(text.charAt(i + 1)));

                if (index < 16) {
                    this.isBold = false;
                    this.isStrikethrough = false;
                    this.isUnderline = false;
                    this.isItalic = false;
                    if (index < 0) index = 15;
                    if (shadow) index += 16;
                    final int j1 = this.colorCode[index];
                    g = (j1 >> 16 & 0xFF) / 255.0f;
                    h = (j1 >> 8 & 0xFF) / 255.0f;
                    k = (j1 & 0xFF) / 255.0f;
                } else if (index != 16) {
                    if (index == 17) this.isBold = true;
                    else if (index == 18) this.isStrikethrough = true;
                    else if (index == 19) this.isUnderline = true;
                    else if (index == 20) this.isItalic = true;
                    else {
                        this.isBold = false;
                        this.isStrikethrough = false;
                        this.isUnderline = false;
                        this.isItalic = false;
                    }
                }

                ++i;
            } else {
                page = this.getPage();
                page.bind();
                this.doDraw(page.drawChar(matrices, ch, this.posX, this.posY, g, k, h, (color >> 24 & 0xFF) / 255.0f), page);
            }
        }

        page.unbind();
        matrices.pop();
    }

    private void renderStringAtPos(final MatrixStack matrices, final CharSequence text, final float scale, final boolean shadow, final int color) {
        GlyphPage page = this.getPage();

        float g = (color >> 16 & 0xFF) / 255.0f;
        float h = (color >> 8 & 0xFF) / 255.0f;
        float k = (color & 0xFF) / 255.0f;

        matrices.push();
        matrices.scale(scale, scale, scale);

        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        page.bind();

        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i);

            if (ch == '�' && i + 1 < text.length()) {
                int index = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(text.charAt(i + 1)));

                if (index < 16) {
                    this.isBold = false;
                    this.isStrikethrough = false;
                    this.isUnderline = false;
                    this.isItalic = false;
                    if (index < 0) index = 15;
                    if (shadow) index += 16;
                    final int j1 = this.colorCode[index];
                    g = (j1 >> 16 & 0xFF) / 255.0f;
                    h = (j1 >> 8 & 0xFF) / 255.0f;
                    k = (j1 & 0xFF) / 255.0f;
                } else if (index != 16) {
                    if (index == 17) this.isBold = true;
                    else if (index == 18) this.isStrikethrough = true;
                    else if (index == 19) this.isUnderline = true;
                    else if (index == 20) this.isItalic = true;
                    else {
                        this.isBold = false;
                        this.isStrikethrough = false;
                        this.isUnderline = false;
                        this.isItalic = false;
                    }
                }

                ++i;
            } else {
                page = this.getPage();
                page.bind();
                this.doDraw(page.drawChar(matrices, ch, this.posX, this.posY, g, k, h, (color >> 24 & 0xFF) / 255.0f), page);
            }
        }
        page.unbind();
        matrices.pop();
    }

    private void doDraw(final float f, final GlyphPage page) {
        if (this.isStrikethrough) {
            final BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

            buffer.vertex(this.posX, this.posY + page.getMaxHeight() / 2, 0.0f);
            buffer.vertex(this.posX + f, this.posY + page.getMaxHeight() / 2, 0.0f);
            buffer.vertex(this.posX + f, this.posY + page.getMaxHeight() / 2 - 1.0f, 0.0f);
            buffer.vertex(this.posX, this.posY + page.getMaxHeight() / 2 - 1.0f, 0.0f);

            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
        
        if (this.isUnderline) {
            final BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            final int l = this.isUnderline ? -1 : 0;

            buffer.vertex(this.posX + l, this.posY + page.getMaxHeight(), 0.0f);
            buffer.vertex(this.posX + f, this.posY + page.getMaxHeight(), 0.0f);
            buffer.vertex(this.posX + f, this.posY + page.getMaxHeight() - 1.0f, 0.0f);
            buffer.vertex(this.posX + l, this.posY + page.getMaxHeight() - 1.0f, 0.0f);

            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }

        this.posX += f;
    }

    private GlyphPage getPage() {
        if (this.isBold && this.isItalic) return this.boldItalic;
        if (this.isBold) return this.bold;
        if (this.isItalic) return this.italic;
        return this.regular;
    }

    private void resetStyles() {
        this.isBold = false;
        this.isItalic = false;
        this.isUnderline = false;
        this.isStrikethrough = false;
    }

    public int getHeight() {
        return this.regular.getMaxHeight() / 2;
    }

    public int getStringWidth(final CharSequence text) {
        if (text == null) return 0;

        int width = 0;
        boolean on = false;

        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i);

            if (ch == '\ufffd') on = true;
            else if (on && ch >= '0' && ch <= 'r') {
                final int index = "0123456789abcdefklmnor".indexOf(ch);
                if (index < 16) {
                    this.isBold = false;
                    this.isItalic = false;
                } else if (index == 17) this.isBold = true;
                else if (index == 20) this.isItalic = true;
                else if (index == 21) {
                    this.isBold = false;
                    this.isItalic = false;
                }

                ++i;
                on = false;
            } else {
                if (on) --i;
                width += (int) (this.getPage().getWidth(text.charAt(i)) - 8.0f);
            }
        }

        return width / 2;
    }

    public CharSequence trimStringToWidth(final CharSequence text, final int width) {
        return this.trimStringToWidth(text, width, false);
    }

    public CharSequence trimStringToWidth(final CharSequence text, final int maxWidth, final boolean reverse) {
        final StringBuilder sb = new StringBuilder();
        boolean on = false;
        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        int width = 0;

        while (j >= 0 && j < text.length() && j < maxWidth) {
            char ch = text.charAt(j);
            if (ch == '\ufffd') on = true;
            else if (on && ch >= '0' && ch <= 'r') {
                final int index = "0123456789abcdefklmnor".indexOf(ch);
                if (index < 16) {
                    this.isBold = false;
                    this.isItalic = false;
                } else if (index == 17) this.isBold = true;
                else if (index == 20) this.isItalic = true;
                else if (index == 21) {
                    this.isBold = false;
                    this.isItalic = false;
                }
                ++j;
                on = false;
            } else {
                if (on) --j;
                ch = text.charAt(j);
                width += (int) ((this.getPage().getWidth(ch) - 8.0f) / 2.0f);
            }

            if (j > width) break;

            if (reverse) sb.insert(0, ch);
            else sb.append(ch);

            j += k;
        }

        return EncryptedString.of(sb.toString());
    }
}