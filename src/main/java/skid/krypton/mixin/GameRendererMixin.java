package skid.krypton.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import skid.krypton.Krypton;
import skid.krypton.event.events.Render3DEvent;
import skid.krypton.event.events.RenderOreEvent;
import skid.krypton.manager.EventManager;
import skid.krypton.module.modules.misc.Freecam;
import skid.krypton.utils.meteor.render.Renderer3D;

import static skid.krypton.Krypton.mc;

@Mixin({GameRenderer.class})
public abstract class GameRendererMixin {
    @Shadow protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

    @Shadow public abstract Matrix4f getBasicProjectionMatrix(double fov);

    @Shadow @Final private Camera camera;
    @Unique
    private Renderer3D renderer;
    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Shadow
    protected abstract void tiltViewWhenHurt(MatrixStack matrices, float tickDelta);

    @Unique
    private final MatrixStack matrices = new MatrixStack();

    @Inject(method = {"renderWorld"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1)})
    private void onWorldRender(final RenderTickCounter rtc, final CallbackInfo ci, @Local MatrixStack matrixStack, @Local(ordinal = 1) Matrix4f matrix4f2, @Local(ordinal = 1) float tickDelta) {
        if (renderer == null) renderer = new Renderer3D();
        mc.getProfiler().push("krypton" + "_render");
        RenderSystem.getModelViewStack().pushMatrix().mul(matrix4f2);
        matrices.push();
        tiltViewWhenHurt(matrices, camera.getLastTickDelta());
        if (mc.options.getBobView().getValue()) bobView(matrices, camera.getLastTickDelta());
        RenderSystem.getModelViewStack().mul(matrices.peek().getPositionMatrix().invert());
        matrices.pop();
        RenderSystem.applyModelViewMatrix();
        RenderOreEvent event = RenderOreEvent.get(matrixStack, renderer, tickDelta, camera.getPos().x, camera.getPos().y, camera.getPos().z);
        EventManager.b(event);
        EventManager.b(new Render3DEvent(new MatrixStack(), this.getBasicProjectionMatrix(this.getFov(this.camera, rtc.getTickDelta(true), true)), rtc.getTickDelta(true)));
        renderer.begin();
        renderer.render(matrixStack);
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.applyModelViewMatrix();
        mc.getProfiler().pop();
    }

    @Inject(method = {"shouldRenderBlockOutline"}, at = {@At("HEAD")}, cancellable = true)
    private void onShouldRenderBlockOutline(final CallbackInfoReturnable<Boolean> cir) {
        if (Krypton.INSTANCE.getModuleManager().getModuleByClass(Freecam.class).isEnabled()) {
            cir.setReturnValue(false);
        }
    }
}