package com.h1ggsk.radon.utils;

import com.h1ggsk.radon.Radon;
import com.h1ggsk.radon.utils.state.CircleRenderState;
import com.h1ggsk.radon.utils.state.RoundedOutlineRenderState;
import com.h1ggsk.radon.utils.state.RoundedQuadRenderState;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class RenderUtils {
    public static VertexSorter vertexSorter;
    public static boolean rendering3D;

    public static Vec3d getCameraPos() {
        return getCamera().getPos();
    }

    public static Camera getCamera() {
        return Radon.mc.getBlockEntityRenderDispatcher().camera;
    }

    public static double deltaTime() {
        double n;
        if (Radon.mc.getCurrentFps() > 0) {
            n = 1.0 / Radon.mc.getCurrentFps();
        } else {
            n = 1.0;
        }
        return n;
    }

    public static float fast(final float n, final float n2, final float n3) {
        return (1.0f - MathHelper.clamp((float) (deltaTime() * n3), 0.0f, 1.0f)) * n + MathHelper.clamp((float) (deltaTime() * n3), 0.0f, 1.0f) * n2;
    }

    public static Vec3d getPlayerLookVec(final PlayerEntity playerEntity) {
        final float cos = MathHelper.cos(playerEntity.getYaw() * 0.017453292f - 3.1415927f);
        final float sin = MathHelper.sin(playerEntity.getYaw() * 0.017453292f - 3.1415927f);
        final float cos2 = MathHelper.cos(playerEntity.getPitch() * 0.017453292f);
        return new Vec3d(sin * cos2, MathHelper.sin(playerEntity.getPitch() * 0.017453292f), cos * cos2).normalize();
    }

    public static void unscaledProjection() {
        RenderUtils.vertexSorter = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0f, (float) Radon.mc.getWindow().getFramebufferWidth(), (float) Radon.mc.getWindow().getFramebufferHeight(), 0.0f, 1000.0f, 21000.0f), VertexSorter.BY_Z);
        RenderUtils.rendering3D = false;
    }

    public static void scaledProjection() {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0f, (float) (Radon.mc.getWindow().getFramebufferWidth() / Radon.mc.getWindow().getScaleFactor()), (float) (Radon.mc.getWindow().getFramebufferHeight() / Radon.mc.getWindow().getScaleFactor()), 0.0f, 1000.0f, 21000.0f), RenderUtils.vertexSorter);
        RenderUtils.rendering3D = true;
    }

    public static void renderRoundedQuad(final DrawContext context, final Color color, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9) {
        context.state.addSimpleElement(new RoundedQuadRenderState(
                context.getMatrices(),
                context.scissorStack.peekLast(),
                color,
                n, n2, n3, n4, n5, n6, n7, n8, n9
        ));
    }

    public static void renderRoundedQuad(final DrawContext context, final Color color, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        renderRoundedQuad(context, color, n, n2, n3, n4, n5, n5, n5, n5, n6);
    }

    private static void setup() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private static void cleanup() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    /**
     * very broken
     * FIXME!!!!!
     */
    public static void renderRoundedOutline(final DrawContext drawContext, final Color color, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10) {
        drawContext.state.addSimpleElement(new RoundedOutlineRenderState(drawContext.getMatrices(), drawContext.scissorStack.peekLast(), color, n, n2, n3, n4, n5, n6, n7, n8, n9, n10));
    }

    public static void setScissorRegion(final int n, final int n2, final int n3, final int n4) {
        final MinecraftClient instance = MinecraftClient.getInstance();
        final Screen currentScreen = instance.currentScreen;
        int n5;
        if (instance.currentScreen == null) {
            n5 = 0;
        } else {
            n5 = currentScreen.height - n4;
        }
        final double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        GL11.glScissor((int) (n * scaleFactor), (int) (n5 * scaleFactor), (int) ((n3 - n) * scaleFactor), (int) ((n4 - n2) * scaleFactor));
        GL11.glEnable(3089);
    }

    public static void renderCircle(final DrawContext context, final Color color, final double centerX, final double centerY, final double radius, final int resolution) {
        context.state.addSimpleElement(
                new CircleRenderState(
                        context.getMatrices(),
                        context.scissorStack.peekLast(),
                        color,
                        centerX,
                        centerY,
                        radius,
                        resolution
                )
        );
    }

    public static void renderShaderRect(final MatrixStack matrixStack, final Color color, final Color color2, final Color color3, final Color color4, final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        final BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        begin.vertex(matrixStack.peek().getPositionMatrix(), n - 10.0f, n2 - 10.0f, 0.0f);
        begin.vertex(matrixStack.peek().getPositionMatrix(), n - 10.0f, n2 + n4 + 20.0f, 0.0f);
        begin.vertex(matrixStack.peek().getPositionMatrix(), n + n3 + 20.0f, n2 + n4 + 20.0f, 0.0f);
        begin.vertex(matrixStack.peek().getPositionMatrix(), n + n3 + 20.0f, n2 - 10.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        RenderSystem.disableBlend();
    }

    public static MatrixStack matrixFrom(final double n, final double n2, final double n3) {
        final MatrixStack matrixStack = new MatrixStack();
        final Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrixStack.translate(n - camera.getPos().x, n2 - camera.getPos().y, n3 - camera.getPos().z);
        return matrixStack;
    }

    public static void renderQuad(final MatrixStack matrixStack, final float n, final float n2, final float n3, final float n4, final int n5) {
        final float n6 = (n5 >> 24 & 0xFF) / 255.0f;
        final float n7 = (n5 >> 16 & 0xFF) / 255.0f;
        final float n8 = (n5 >> 8 & 0xFF) / 255.0f;
        final float n9 = (n5 & 0xFF) / 255.0f;
        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.translate(n, n2, 0.0);
        final Tessellator instance = Tessellator.getInstance();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        final BufferBuilder begin = instance.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        begin.vertex(0.0f, 0.0f, 0.0f).color(n7, n8, n9, n6);
        begin.vertex(0.0f, n4, 0.0f).color(n7, n8, n9, n6);
        begin.vertex(n3, n4, 0.0f).color(n7, n8, n9, n6);
        begin.vertex(n3, 0.0f, 0.0f).color(n7, n8, n9, n6);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        RenderSystem.disableBlend();
        matrixStack.pop();
    }

    public static void renderFilledBox(final MatrixStack matrixStack, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final Color color) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        RenderSystem.setShader((Supplier) GameRenderer::getPositionProgram);
        final BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION);
        final Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
        begin.vertex(positionMatrix, n, n2, n3);
        begin.vertex(positionMatrix, n, n2, n3);
        begin.vertex(positionMatrix, n, n2, n3);
        begin.vertex(positionMatrix, n, n2, n6);
        begin.vertex(positionMatrix, n, n5, n3);
        begin.vertex(positionMatrix, n, n5, n6);
        begin.vertex(positionMatrix, n, n5, n6);
        begin.vertex(positionMatrix, n, n2, n6);
        begin.vertex(positionMatrix, n4, n5, n6);
        begin.vertex(positionMatrix, n4, n2, n6);
        begin.vertex(positionMatrix, n4, n2, n6);
        begin.vertex(positionMatrix, n4, n2, n3);
        begin.vertex(positionMatrix, n4, n5, n6);
        begin.vertex(positionMatrix, n4, n5, n3);
        begin.vertex(positionMatrix, n4, n5, n3);
        begin.vertex(positionMatrix, n4, n2, n3);
        begin.vertex(positionMatrix, n, n5, n3);
        begin.vertex(positionMatrix, n, n2, n3);
        begin.vertex(positionMatrix, n, n2, n3);
        begin.vertex(positionMatrix, n4, n2, n3);
        begin.vertex(positionMatrix, n, n2, n6);
        begin.vertex(positionMatrix, n4, n2, n6);
        begin.vertex(positionMatrix, n4, n2, n6);
        begin.vertex(positionMatrix, n, n5, n3);
        begin.vertex(positionMatrix, n, n5, n3);
        begin.vertex(positionMatrix, n, n5, n6);
        begin.vertex(positionMatrix, n4, n5, n3);
        begin.vertex(positionMatrix, n4, n5, n6);
        begin.vertex(positionMatrix, n4, n5, n6);
        begin.vertex(positionMatrix, n4, n5, n6);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void renderLine(final MatrixStack matrixStack, final Color color, final Vec3d vec3d, final Vec3d vec3d2) {
        matrixStack.push();
        final Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
        if (com.h1ggsk.radon.module.modules.client.Radon.enableMSAA.getValue()) {
            GL11.glEnable(32925);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
        }
        GL11.glDepthFunc(519);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        genericAABBRender(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR, GameRenderer::getPositionColorProgram, positionMatrix, vec3d, vec3d2.subtract(vec3d), color, (bufferBuilder, n, n3, n5, n7, n9, n11, n13, n15, n17, n19, matrix4f) -> {
            bufferBuilder.vertex(matrix4f, n, n3, n5).color(n13, n15, n17, n19);
            bufferBuilder.vertex(matrix4f, n7, n9, n11).color(n13, n15, n17, n19);
        });
        GL11.glDepthFunc(515);
        RenderSystem.disableBlend();
        if (com.h1ggsk.radon.module.modules.client.Radon.enableMSAA.getValue()) {
            GL11.glDisable(2848);
            GL11.glDisable(32925);
        }
        matrixStack.pop();
    }

    public static void drawItem(final DrawContext drawContext, final ItemStack itemStack, final int n, final int n2, final float n3, final int n4) {
        if (itemStack.isEmpty()) {
            return;
        }
        final float n5 = n3 / 16.0f;
        final Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.translate((float) n, (float) n2, (float) n4);
        matrices.scale(n5, n5);
        drawContext.drawItem(itemStack, 0, 0);
        matrices.popMatrix();
    }

    private static void genericAABBRender(VertexFormat.DrawMode mode, VertexFormat format, Supplier<ShaderProgram> shader, Matrix4f stack, Vec3d start, Vec3d dimensions, Color color, RenderAction action) {
        float red = color.getRed() / 255f;
        float green = color.getGreen() / 255f;
        float blue = color.getBlue() / 255f;
        float alpha = color.getAlpha() / 255f;
        Vec3d end = start.add(dimensions);
        float x1 = (float) start.x;
        float y1 = (float) start.y;
        float z1 = (float) start.z;
        float x2 = (float) end.x;
        float y2 = (float) end.y;
        float z2 = (float) end.z;
        useBuffer(mode, format, shader, bufferBuilder -> action.run(bufferBuilder, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, stack));
    }


    interface RenderAction {
        void run(BufferBuilder buffer, float x, float y, float z, float x1, float y1, float z1, float red, float green, float blue, float alpha, Matrix4f matrix);
    }

    private static void useBuffer(final VertexFormat.DrawMode vertexFormat$DrawMode, final VertexFormat vertexFormat, final Supplier<ShaderProgram> shader, final Consumer<BufferBuilder> consumer) {
        final BufferBuilder begin = Tessellator.getInstance().begin(vertexFormat$DrawMode, vertexFormat);
        consumer.accept(begin);
        setup();
        RenderSystem.setShader(shader);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        cleanup();
    }

    static {
        RenderUtils.rendering3D = true;
    }
}