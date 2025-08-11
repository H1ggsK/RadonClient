package com.h1ggsk.radon.font;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

@SuppressWarnings("unused")
public final class GlyphPage {
    private int imageSize;
    private int maxHeight;
    private final Font font;
    private final boolean antiAlias;
    private final boolean fractionalMetrics;
    private final HashMap<Character, Glyph> glyphs;
    private BufferedImage img;
    private AbstractTexture texture;

    public GlyphPage(final Font font, final boolean antiAlias, final boolean fractionalMetrics) {
        this.maxHeight = -1;
        this.glyphs = new HashMap<>();
        this.font = font;
        this.antiAlias = antiAlias;
        this.fractionalMetrics = fractionalMetrics;
    }

    public void generate(final char[] chars) {
        double width = -1.0;
        double height = -1.0;

        final FontRenderContext frc = new FontRenderContext(new AffineTransform(), this.antiAlias, this.fractionalMetrics);

        for (char item : chars) {
            final Rectangle2D bounds = this.font.getStringBounds(Character.toString(item), frc);
            if (width < bounds.getWidth()) width = bounds.getWidth();
            if (height < bounds.getHeight()) height = bounds.getHeight();
        }

        final double maxWidth = width + 2.0;
        final double maxHeight = height + 2.0;
        this.imageSize = (int) Math.ceil(Math.max(Math.ceil(Math.sqrt(maxWidth * maxWidth * chars.length) / maxWidth), Math.ceil(Math.sqrt(maxHeight * maxHeight * chars.length) / maxHeight)) * Math.max(maxWidth, maxHeight)) + 1;
        this.img = new BufferedImage(this.imageSize, this.imageSize, 2);
        final Graphics2D graphics = this.img.createGraphics();
        graphics.setFont(this.font);
        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, this.imageSize, this.imageSize);
        graphics.setColor(Color.white);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        final FontMetrics metrics = graphics.getFontMetrics();

        int currentHeight = 0;
        int posX = 0;
        int posY = 1;

        for (final char c : chars) {
            final Glyph glyph = new Glyph();
            final Rectangle2D bounds = metrics.getStringBounds(Character.toString(c), graphics);

            glyph.width = bounds.getBounds().width + 8;
            glyph.height = bounds.getBounds().height;

            if (posX + glyph.width >= this.imageSize) {
                posX = 0;
                posY += currentHeight;
                currentHeight = 0;
            }

            glyph.x = posX;
            glyph.y = posY;

            if (glyph.height > this.maxHeight) this.maxHeight = glyph.height;
            if (glyph.height > currentHeight) currentHeight = glyph.height;

            graphics.drawString(Character.toString(c), posX + 2, posY + metrics.getAscent());
            posX += glyph.width;
            this.glyphs.put(c, glyph);
        }
    }

    public void setup() {
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(this.img, "png", output);
            final byte[] byteArray = output.toByteArray();
            final ByteBuffer data = BufferUtils.createByteBuffer(byteArray.length).put(byteArray);
            data.flip();
            this.texture = new NativeImageBackedTexture(NativeImage.read(data));
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
        }
    }

    public void bind() {
        RenderSystem.setShaderTexture(0, this.texture.getGlId());
    }

    public void unbind() {
        RenderSystem.setShaderTexture(0, 0);
    }

    public float drawChar(MatrixStack stack, char ch, float x, float y, float r, float b, float g, float alpha) {
        final Glyph glyph = glyphs.get(ch);
        if (glyph == null) return 0;

        final float pageX = glyph.x / (float) imageSize;
        final float pageY = glyph.y / (float) imageSize;

        final float pageWidth = glyph.width / (float) imageSize;
        final float pageHeight = glyph.height / (float) imageSize;

        final float width = glyph.width;
        final float height = glyph.height;

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

        bind();

        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        builder.vertex(stack.peek().getPositionMatrix(), x, y + height, 0).color(r, g, b, alpha).texture(pageX, pageY + pageHeight);
        builder.vertex(stack.peek().getPositionMatrix(), x + width, y + height, 0).color(r, g, b, alpha).texture(pageX + pageWidth, pageY + pageHeight);
        builder.vertex(stack.peek().getPositionMatrix(), x + width, y, 0).color(r, g, b, alpha).texture(pageX + pageWidth, pageY);
        builder.vertex(stack.peek().getPositionMatrix(), x, y, 0).color(r, g, b, alpha).texture(pageX, pageY);

        BufferRenderer.drawWithGlobalProgram(builder.end());

        unbind();

        return width - 8;
    }

    public float getWidth(final char c) {
        return (float) this.glyphs.get(c).width;
    }

    public boolean isAntiAlias() {
        return this.antiAlias;
    }

    public boolean isFractionalMetrics() {
        return this.fractionalMetrics;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public static final class Glyph {
        private int x;
        private int y;
        private int width;
        private int height;

        Glyph(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        Glyph() {}

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }
}