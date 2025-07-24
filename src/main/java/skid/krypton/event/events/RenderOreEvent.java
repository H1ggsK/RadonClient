package skid.krypton.event.events;

import net.minecraft.client.util.math.MatrixStack;
import skid.krypton.event.CancellableEvent;
import skid.krypton.utils.meteor.render.Renderer3D;

public class RenderOreEvent extends CancellableEvent {
    private static final RenderOreEvent INSTANCE = new RenderOreEvent();

    public MatrixStack matrices;
    public Renderer3D renderer;
    public float tickDelta;
    public double offsetX, offsetY, offsetZ;

    public static RenderOreEvent get(MatrixStack matrices, Renderer3D renderer, float tickDelta, double offsetX, double offsetY, double offsetZ) {
        INSTANCE.matrices = matrices;
        INSTANCE.renderer = renderer;
        INSTANCE.tickDelta = tickDelta;
        INSTANCE.offsetX = offsetX;
        INSTANCE.offsetY = offsetY;
        INSTANCE.offsetZ = offsetZ;
        return INSTANCE;
    }
}