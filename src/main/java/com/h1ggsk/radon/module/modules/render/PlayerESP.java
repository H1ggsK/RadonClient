package com.h1ggsk.radon.module.modules.render;

import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.Render3DEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.modules.client.Radon;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.ColorUtil;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.RenderUtils;
import com.h1ggsk.radon.utils.Utils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public final class PlayerESP extends Module {
    private final NumberSetting alpha = new NumberSetting(EncryptedString.of("Alpha"), 0.0, 255.0, 100.0, 1.0);
    private final NumberSetting lineWidth = new NumberSetting(EncryptedString.of("Line width"), 1.0, 10.0, 1.0, 1.0);
    private final BooleanSetting tracers = new BooleanSetting(EncryptedString.of("Tracers"), true).setDescription(EncryptedString.of("Draws keybindListener line from your player to the other"));

    public PlayerESP() {
        super(EncryptedString.of("Player ESP"), EncryptedString.of("Renders players through walls"), -1, Category.RENDER);
        this.addSettings(this.alpha, this.lineWidth, this.tracers);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventListener
    public void onRender3D(final Render3DEvent render3DEvent) {
        for (final AbstractClientPlayerEntity next : mc.world.getPlayers()) {
            if (next != mc.player) {
                final Camera camera = RenderUtils.getCamera();
                if (camera != null) {
                    final MatrixStack a = render3DEvent.matrixStack;
                    render3DEvent.matrixStack.push();
                    final Vec3d pos = RenderUtils.getCameraPos();
                    a.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    a.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
                    a.translate(-pos.x, -pos.y, -pos.z);
                }
                final double lerp = MathHelper.lerp(RenderTickCounter.ONE.getTickProgress(true), next.lastX, next.getX());
                final double lerp2 = MathHelper.lerp(RenderTickCounter.ONE.getTickProgress(true), next.lastY, next.getY());
                final double lerp3 = MathHelper.lerp(RenderTickCounter.ONE.getTickProgress(true), next.lastZ, next.getZ());
                RenderUtils.renderFilledBox(render3DEvent.matrixStack, (float) lerp - next.getWidth() / 2.0f, (float) lerp2, (float) lerp3 - next.getWidth() / 2.0f, (float) lerp + next.getWidth() / 2.0f, (float) lerp2 + next.getHeight(), (float) lerp3 + next.getWidth() / 2.0f, Utils.getMainColor(this.alpha.getIntValue(), 1).brighter());
                if (this.tracers.getValue()) {
                    RenderUtils.renderLine(render3DEvent.matrixStack, Utils.getMainColor(255, 1), mc.crosshairTarget.getPos(), next.getLerpedPos(RenderTickCounter.ONE.getTickProgress(true)));
                }
                render3DEvent.matrixStack.pop();
            }
        }
    }

    /*
    private void renderPlayerOutline(final PlayerEntity playerEntity, final Color color, final MatrixStack matrixStack) {
        final float n = color.brighter().getRed() / 255.0f;
        final float n2 = color.brighter().getGreen() / 255.0f;
        final float n3 = color.brighter().getBlue() / 255.0f;
        final float n4 = color.brighter().getAlpha() / 255.0f;
        final Camera camera = mc.gameRenderer.getCamera();
        final Vec3d subtract = playerEntity.getLerpedPos(RenderTickCounter.ONE.getTickProgress(true)).subtract(camera.getPos());
        final float n5 = (float) subtract.x;
        final float n6 = (float) subtract.y;
        final float n7 = (float) subtract.z;
        final double radians = Math.toRadians(camera.getYaw() + 90.0f);
        final double n8 = Math.sin(radians) * (playerEntity.getWidth() / 1.7);
        final double n9 = Math.cos(radians) * (playerEntity.getWidth() / 1.7);
        matrixStack.push();
        final Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
        RenderSystem.setShader((Supplier) GameRenderer::getPositionColorProgram);
        if (Radon.enableMSAA.getValue()) {
            GL11.glEnable(32925);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
        }
        GL11.glDepthFunc(519);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        GL11.glLineWidth((float) this.lineWidth.getIntValue());
        final BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        begin.vertex(positionMatrix, n5 + (float) n8, n6, n7 + (float) n9).color(n, n2, n3, n4);
        begin.vertex(positionMatrix, n5 - (float) n8, n6, n7 - (float) n9).color(n, n2, n3, n4);
        begin.vertex(positionMatrix, n5 - (float) n8, n6, n7 - (float) n9).color(n, n2, n3, n4);
        begin.vertex(positionMatrix, n5 - (float) n8, n6 + playerEntity.getHeight(), n7 - (float) n9).color(n, n2, n3, n4);
        begin.vertex(positionMatrix, n5 - (float) n8, n6 + playerEntity.getHeight(), n7 - (float) n9).color(n, n2, n3, n4);
        begin.vertex(positionMatrix, n5 + (float) n8, n6 + playerEntity.getHeight(), n7 + (float) n9).color(n, n2, n3, n4);
        begin.vertex(positionMatrix, n5 + (float) n8, n6 + playerEntity.getHeight(), n7 + (float) n9).color(n, n2, n3, n4);
        begin.vertex(positionMatrix, n5 + (float) n8, n6, n7 + (float) n9).color(n, n2, n3, n4);
        begin.vertex(positionMatrix, n5 + (float) n8, n6, n7 + (float) n9).color(n, n2, n3, n4);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        GL11.glDepthFunc(515);
        GL11.glLineWidth(1.0f);
        RenderSystem.disableBlend();
        if (Radon.enableMSAA.getValue()) {
            GL11.glDisable(2848);
            GL11.glDisable(32925);
        }
        matrixStack.pop();
    }
     */

    private Color getColorWithAlpha(final int a) {
        final int f = Radon.redColor.getIntValue();
        final int f2 = Radon.greenColor.getIntValue();
        final int f3 = Radon.blueColor.getIntValue();
        if (Radon.enableRainbowEffect.getValue()) {
            return ColorUtil.a(1, a);
        }
        return new Color(f, f2, f3, a);
    }
}
