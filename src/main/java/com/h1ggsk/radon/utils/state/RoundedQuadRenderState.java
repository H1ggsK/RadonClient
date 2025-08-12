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

import java.awt.*;


public record RoundedQuadRenderState(Matrix3x2f matrices, ScreenRect scissorArea, ScreenRect bounds, Color color, double x1, double y1, double x2, double y2, double radius1, double radius2, double radius3, double radius4, double resolution) implements SimpleGuiElementRenderState {
    private static final RenderPipeline PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
                    .withBlend(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO))
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_FAN)
                    .withLocation(Identifier.of("radon", "rounded_quad"))
                    .build()
    );

    public RoundedQuadRenderState(Matrix3x2f matrices, ScreenRect scissorArea, ScreenRect bounds, Color color, double x1, double y1, double x2, double y2, double radius, double resolution) {
        this(matrices, scissorArea, bounds, color, x1, y1, x2, y2, radius, radius, radius, radius, resolution);
    }

    public RoundedQuadRenderState(Matrix3x2fStack matrices, ScreenRect scissorArea, ScreenRect bounds, Color color, double x1, double y1, double x2, double y2, double radius1, double radius2, double radius3, double radius4, double resolution) {
        this(new Matrix3x2f(matrices), scissorArea, bounds, color, x1, y1, x2, y2, radius1, radius2, radius3, radius4, resolution);
    }

    public RoundedQuadRenderState(Matrix3x2fStack matrices, ScreenRect scissorArea, ScreenRect bounds, Color color, double x1, double y1, double x2, double y2, double radius, double resolution) {
        this(new Matrix3x2f(matrices), scissorArea, bounds, color, x1, y1, x2, y2, radius, radius, radius, radius, resolution);
    }

    public RoundedQuadRenderState(Matrix3x2fStack matrices, ScreenRect scissorArea, Color color, double x1, double y1, double x2, double y2, double radius1, double radius2, double radius3, double radius4, double resolution) {
        this(new Matrix3x2f(matrices), scissorArea, createBounds((int) x1, (int) y1, (int) x2, (int) y2, matrices, scissorArea), color, x1, y1, x2, y2, radius1, radius2, radius3, radius4, resolution);
    }

    public RoundedQuadRenderState(Matrix3x2fStack matrices, ScreenRect scissorArea, Color color, double x1, double y1, double x2, double y2, double radius, double resolution) {
        this(new Matrix3x2f(matrices), scissorArea, createBounds((int) x1, (int) y1, (int) x2, (int) y2, matrices, scissorArea), color, x1, y1, x2, y2, radius, radius, radius, radius, resolution);
    }

    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        // {xish: double, yish: double, radius: double}[]
        double[][] array = new double[][]{
                new double[]{x2 - radius4, y2 - radius4, radius4},
                new double[]{x2 - radius2, y1 + radius2, radius2},
                new double[]{x1 + radius1, y1 + radius1, radius1},
                new double[]{x1 + radius3, y2 - radius3, radius3}
        };
        for (int i = 0; i < 4; ++i) {
            double[] array2 = array[i];
            double radius = array2[2];
            for (double angdeg = i * 90.0; angdeg < 90.0 + i * 90.0; angdeg += 90.0 / resolution) {
                double radians = Math.toRadians(angdeg);
                vertices.vertex(
                                matrices,
                                (float) array2[0] + (float) (Math.sin((float) radians) * radius),
                                (float) array2[1] + (float) (Math.cos((float) radians) * radius),
                                depth
                        )
                        .color(r, g, b, a);
            }
            double radians2 = Math.toRadians(90.0 + i * 90.0);
            vertices.vertex(
                            matrices,
                            (float) array2[0] + (float) (Math.sin((float) radians2) * radius),
                            (float) array2[1] + (float) (Math.cos((float) radians2) * radius),
                            depth
                    )
                    .color(r, g, b, a);
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
