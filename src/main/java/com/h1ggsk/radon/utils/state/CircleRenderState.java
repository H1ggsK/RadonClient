package com.h1ggsk.radon.utils.state;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
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
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.awt.*;

import static net.minecraft.client.gl.RenderPipelines.register;

public record CircleRenderState(Matrix3x2f matrices, ScreenRect scissorArea, ScreenRect bounds, Color color, double centerX, double centerY, double radius, int resolution) implements SimpleGuiElementRenderState {
    private static final RenderPipeline PIPELINE = register(
            RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
                    .withLocation(Identifier.of("radon", "circle_pipeline"))
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withBlend(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO))
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_FAN)
            .build()
    );

    public CircleRenderState(Matrix3x2fStack matrices, ScreenRect scissorArea, ScreenRect bounds, Color color, double centerX, double centerY, double radius, int resolution) {
        this(new Matrix3x2f(matrices), scissorArea, bounds, color, centerX, centerY, radius, resolution);
    }

    public CircleRenderState(Matrix3x2f matrices, ScreenRect scissorArea, Color color, double centerX, double centerY, double radius, int resolution) {
        this(matrices, scissorArea, createBounds((int) (centerX - radius), (int) (centerY - radius), (int) (centerX + radius), (int) (centerY + radius), matrices, scissorArea), color, centerX, centerY, radius, resolution);
    }

    public CircleRenderState(Matrix3x2fStack matrices, ScreenRect scissorArea, Color color, double centerX, double centerY, double radius, int resolution) {
        this(new Matrix3x2f(matrices), scissorArea, color, centerX, centerY, radius, resolution);
    }

    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        final int clamp = MathHelper.clamp(resolution, 4, 360);
        final int rgb = color.getRGB();
        for (int i = 0; i < 360; i += Math.min(360 / clamp, 360 - i)) {
            final double radians = Math.toRadians(i);
            vertices.vertex(
                    matrices,
                    (float) (centerX + Math.sin(radians) * radius),
                    (float) (centerY + Math.cos(radians) * radius),
                    depth
            ).color(
                    (rgb >> 16 & 0xFF) / 255.0f,
                    (rgb >> 8 & 0xFF) / 255.0f,
                    (rgb & 0xFF) / 255.0f,
                    (rgb >> 24 & 0xFF) / 255.0f
            );
        }
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