package com.h1ggsk.radon.utils.state;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.awt.Color;

import static net.minecraft.client.gl.RenderPipelines.register;

/**
 * Technically broken, should render a rounded outline around the given rect.
 * FIXME!!!
 */
public record RoundedOutlineRenderState(Matrix3x2f matrices, ScreenRect scissorArea, ScreenRect bounds, Color color, double x1, double y1, double x2, double y2, double radius1, double radius2, double radius3, double radius4, double maybeOffset, double resolution) implements SimpleGuiElementRenderState {
    private static final RenderPipeline PIPELINE = register(
            RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
                    .withLocation(Identifier.of("radon", "rounded_quad_outline"))
                    .withBlend(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO))
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_FAN)
                    .build()
    );

    public RoundedOutlineRenderState(Matrix3x2fStack matrices, ScreenRect scissorArea, ScreenRect bounds, Color color, double x1, double y1, double x2, double y2, double radius1, double radius2, double radius3, double radius4, double maybeOffset, double resolution) {
        this(new Matrix3x2f(matrices), scissorArea, bounds, color, x1, y1, x2, y2, radius1, radius2, radius3, radius4, maybeOffset, resolution);
    }

    public RoundedOutlineRenderState(Matrix3x2f matrices, ScreenRect scissorArea, Color color, double x1, double y1, double x2, double y2, double radius1, double radius2, double radius3, double radius4, double maybeOffset, double resolution) {
        this(matrices, scissorArea, createBounds((int) x1, (int) y1, (int) x2, (int) y2, matrices, scissorArea), color, x1, y1, x2, y2, radius1, radius2, radius3, radius4, maybeOffset, resolution);
    }

    public RoundedOutlineRenderState(Matrix3x2fStack matrices, ScreenRect scissorArea, Color color, double x1, double y1, double x2, double y2, double radius1, double radius2, double radius3, double radius4, double maybeOffset, double resolution) {
        this(new Matrix3x2f(matrices), scissorArea, createBounds((int) x1, (int) y1, (int) x2, (int) y2, matrices, scissorArea), color, x1, y1, x2, y2, radius1, radius2, radius3, radius4, maybeOffset, resolution);
    }

    public RoundedOutlineRenderState(Matrix3x2fStack matrices, ScreenRect scissorArea, Color color, double x1, double y1, double x2, double y2, double radius, double maybeOffset, double resolution) {
        this(new Matrix3x2f(matrices), scissorArea, createBounds((int) x1, (int) y1, (int) x2, (int) y2, matrices, scissorArea), color, x1, y1, x2, y2, radius, radius, radius, radius, maybeOffset, resolution);
    }

    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        final float r = (float) color.getRed() / 255.0f;
        final float g = (float) color.getGreen() / 255.0f;
        final float b = (float) color.getBlue() / 255.0f;
        final float a = (float) color.getAlpha() / 255.0f;
        // {xish: double, yish: double, radius: double}[]
        final double[][] array = new double[][]{
                new double[]{x2 - radius4, y2 - radius4, radius4},
                new double[]{x2 - radius2, y1 + radius2, radius2},
                new double[]{x1 + radius1, y1 + radius1, radius1},
                new double[]{x1 + radius3, y2 - radius3, radius3}
        };
        for (int i = 0; i < 4; ++i) {
            final double[] maybeOffsets = array[i];
            final double radius = maybeOffsets[2];
            for (double angdeg = i * 90.0; angdeg < 90.0 + i * 90.0; angdeg += 90.0 / resolution) {
                final double radians = Math.toRadians(angdeg);
                final double sin = Math.sin((float) radians);
                final double relativeX = sin * radius;
                final double cos = Math.cos((float) radians);
                final double relativeY = cos * radius;
                vertices.vertex(
                        matrices,
                        (float) maybeOffsets[0] + (float) relativeX,
                        (float) maybeOffsets[1] + (float) relativeY,
                        depth
                ).color(r, g, b, a);
                vertices.vertex(
                        matrices,
                        (float) (maybeOffsets[0] + (float) relativeX + sin * maybeOffset),
                        (float) (maybeOffsets[1] + (float) relativeY + cos * maybeOffset),
                        depth
                ).color(r, g, b, a);
            }
            final double radians = Math.toRadians(90.0 + i * 90.0);
            final double sin2 = Math.sin((float) radians);
            final double relativeX = sin2 * radius;
            final double cos2 = Math.cos((float) radians);
            final double relativeY = cos2 * radius;
            vertices.vertex(
                    matrices,
                    (float) maybeOffsets[0] + (float) relativeX,
                    (float) maybeOffsets[1] + (float) relativeY,
                    depth
            ).color(r, g, b, a);
            vertices.vertex(
                    matrices,
                    (float) (maybeOffsets[0] + (float) relativeX + sin2 * maybeOffset),
                    (float) (maybeOffsets[1] + (float) relativeY + cos2 * maybeOffset),
                    depth
            ).color(r, g, b, a);
        }
        final double[] maybeOffsets = array[0];
        final double radius = maybeOffsets[2];
        vertices.vertex(
                matrices,
                (float) maybeOffsets[0],
                (float) maybeOffsets[1] + (float) radius,
                depth
        ).color(r, g, b, a);
        vertices.vertex(
                matrices,
                (float) maybeOffsets[0],
                (float) (maybeOffsets[1] + (float) radius + maybeOffset),
                depth
        ).color(r, g, b, a);
    }

    @Override
    public RenderPipeline pipeline() {
        return PIPELINE;
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.empty();
    }

    @Nullable
    private static ScreenRect createBounds(int x1, int y1, int x2, int y2, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        ScreenRect screenRect = new ScreenRect(x1, y1, x2 - x1, y2 - y1).transformEachVertex(pose);
        return scissorArea != null
               ? scissorArea.intersection(screenRect)
               : screenRect;
    }

    public static void init() {
    }
}
