package skid.krypton.module.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import skid.krypton.event.EventListener;
import skid.krypton.event.events.Render3DEvent;
import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.module.modules.client.Krypton;
import skid.krypton.module.setting.BooleanSetting;
import skid.krypton.module.setting.NumberSetting;
import skid.krypton.utils.ColorUtil;
import skid.krypton.utils.EncryptedString;
import skid.krypton.utils.Utils;
import skid.krypton.utils.RenderUtils;

import java.awt.*;
import java.util.function.Supplier;

public final class PlayerESP extends Module {
    private final NumberSetting alpha = new NumberSetting(EncryptedString.of("Alpha"), 0.0, 255.0, 100.0, 1.0);
    private final NumberSetting lineWidth = new NumberSetting(EncryptedString.of("Line width"), 1.0, 10.0, 1.0, 1.0);
    private final BooleanSetting tracers = new BooleanSetting(EncryptedString.of("Tracers"), true).setDescription(EncryptedString.of("Draws a line from your player to the other"));

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
        for (final Object next : this.mc.world.getPlayers()) {
            if (next != this.mc.player) {
                final Camera camera = RenderUtils.getCamera();
                if (camera != null) {
                    final MatrixStack a = render3DEvent.matrixStack;
                    render3DEvent.matrixStack.push();
                    final Vec3d pos = RenderUtils.getCameraPos();
                    a.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    a.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
                    a.translate(-pos.x, -pos.y, -pos.z);
                }
                final double lerp = MathHelper.lerp(RenderTickCounter.ONE.getTickDelta(true), ((PlayerEntity) next).prevX, ((PlayerEntity) next).getX());
                final double lerp2 = MathHelper.lerp(RenderTickCounter.ONE.getTickDelta(true), ((PlayerEntity) next).prevY, ((PlayerEntity) next).getY());
                final double lerp3 = MathHelper.lerp(RenderTickCounter.ONE.getTickDelta(true), ((PlayerEntity) next).prevZ, ((PlayerEntity) next).getZ());
                RenderUtils.renderFilledBox(render3DEvent.matrixStack, (float) lerp - ((PlayerEntity) next).getWidth() / 2.0f, (float) lerp2, (float) lerp3 - ((PlayerEntity) next).getWidth() / 2.0f, (float) lerp + ((PlayerEntity) next).getWidth() / 2.0f, (float) lerp2 + ((PlayerEntity) next).getHeight(), (float) lerp3 + ((PlayerEntity) next).getWidth() / 2.0f, Utils.getMainColor(this.alpha.getIntValue(), 1).brighter());
                if (this.tracers.getValue()) {
                    RenderUtils.renderLine(render3DEvent.matrixStack, Utils.getMainColor(255, 1), this.mc.crosshairTarget.getPos(), ((PlayerEntity) next).getLerpedPos(RenderTickCounter.ONE.getTickDelta(true)));
                }
                render3DEvent.matrixStack.pop();
            }
        }
    }

    private void renderPlayerOutline(final PlayerEntity playerEntity, final Color color, final MatrixStack matrixStack) {
        final float n = color.brighter().getRed() / 255.0f;
        final float n2 = color.brighter().getGreen() / 255.0f;
        final float n3 = color.brighter().getBlue() / 255.0f;
        final float n4 = color.brighter().getAlpha() / 255.0f;
        final Camera camera = this.mc.gameRenderer.getCamera();
        final Vec3d subtract = playerEntity.getLerpedPos(RenderTickCounter.ONE.getTickDelta(true)).subtract(camera.getPos());
        final float n5 = (float) subtract.x;
        final float n6 = (float) subtract.y;
        final float n7 = (float) subtract.z;
        final double radians = Math.toRadians(camera.getYaw() + 90.0f);
        final double n8 = Math.sin(radians) * (playerEntity.getWidth() / 1.7);
        final double n9 = Math.cos(radians) * (playerEntity.getWidth() / 1.7);
        matrixStack.push();
        final Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
        RenderSystem.setShader((Supplier) GameRenderer::getPositionColorProgram);
        if (Krypton.enableMSAA.getValue()) {
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
        if (Krypton.enableMSAA.getValue()) {
            GL11.glDisable(2848);
            GL11.glDisable(32925);
        }
        matrixStack.pop();
    }

    private Color getColorWithAlpha(final int a) {
        final int f = Krypton.redColor.getIntValue();
        final int f2 = Krypton.greenColor.getIntValue();
        final int f3 = Krypton.blueColor.getIntValue();
        if (Krypton.enableRainbowEffect.getValue()) {
            return ColorUtil.a(1, a);
        }
        return new Color(f, f2, f3, a);
    }
}
