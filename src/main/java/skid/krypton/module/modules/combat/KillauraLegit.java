package skid.krypton.module.modules.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import skid.krypton.event.EventListener;
import skid.krypton.event.events.MouseUpdateEvent;
import skid.krypton.event.events.StartTickEvent;
import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.module.setting.BooleanSetting;
import skid.krypton.module.setting.MinMaxSetting;
import skid.krypton.module.setting.NumberSetting;
import skid.krypton.utils.EncryptedString;
import skid.krypton.utils.rotation.Rotation;
import skid.krypton.utils.rotation.RotationUtils;

import java.util.Random;

public final class KillauraLegit extends Module {
    private final NumberSetting range = new NumberSetting(EncryptedString.of("Range"), 1.0, 6.0, 4.25, 0.05);
    private final NumberSetting rotationSpeed = new NumberSetting(EncryptedString.of("Rotation Speed"), 10.0, 3600.0, 600.0, 10.0);
    private final NumberSetting fov = new NumberSetting(EncryptedString.of("FOV"), 30.0, 360.0, 360.0, 10.0);
    private final MinMaxSetting delayRange = new MinMaxSetting("Delay", 1.0, 100.0, 1.0, 20.0, 70.0);
    private final BooleanSetting targetInvis = new BooleanSetting("Target Invis", false);

    private Entity target;
    private float serverYaw;
    private float serverPitch;
    private long lastAttackTime;
    private final Random random = new Random();

    public KillauraLegit() {
        super(EncryptedString.of("KillauraLegit"), EncryptedString.of("Attacks players near you"), -1, Category.COMBAT);
        this.addSettings(range, rotationSpeed, fov, delayRange, targetInvis);
    }

    @Override
    public void onEnable() {
        target = null;
        lastAttackTime = 0;
        super.onEnable();
    }

    @EventListener
    public void onTick(StartTickEvent event) {
        if (mc.currentScreen instanceof HandledScreen) {
            target = null;
            return;
        }

        target = null;
        double closestDistance = Double.MAX_VALUE;
        double rangeSq = range.getValue() * range.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity) || entity == mc.player) continue;
            if (!targetInvis.getValue() && ((PlayerEntity) entity).isInvisible()) continue;

            double distance = mc.player.squaredDistanceTo(entity);
            if (distance > rangeSq) continue;

            if (fov.getValue() < 360.0 && getAngleToEntity(entity) > fov.getValue() / 2.0) continue;

            if (!hasLineOfSight(entity.getBoundingBox().getCenter())) continue;

            if (distance < closestDistance) {
                closestDistance = distance;
                target = entity;
            }
        }

        if (target != null) {
            Box box = target.getBoundingBox();
            Rotation needed = RotationUtils.getNeededRotations(box.getCenter());
            Rotation next = RotationUtils.slowlyTurnTowards(needed, rotationSpeed.getFloatValue() / 20F);
            serverYaw = next.yaw();
            serverPitch = next.pitch();

            if (canAttack() && (RotationUtils.isAlreadyFacing(needed) || RotationUtils.isFacingBox(box, range.getValue()))) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                lastAttackTime = System.currentTimeMillis() + getRandomDelay();
            }
        }
    }

    private boolean canAttack() {
        if (target == null) return false;
        if (mc.player.getAttackCooldownProgress(0.5f) < 0.9f) return false;
        return System.currentTimeMillis() >= lastAttackTime;
    }

    @EventListener
    public void onMouseUpdate(MouseUpdateEvent event) {
        if (target == null || mc.player == null) return;

        float yawDiff = MathHelper.wrapDegrees(serverYaw - mc.player.getYaw());
        float pitchDiff = MathHelper.wrapDegrees(serverPitch - mc.player.getPitch());

        if (Math.abs(yawDiff) < 1 && Math.abs(pitchDiff) < 1) return;

        event.setDeltaX(event.getDefaultDeltaX() + (int) yawDiff);
        event.setDeltaY(event.getDefaultDeltaY() + (int) pitchDiff);
    }

    long getRandomDelay() {
        return (long) (delayRange.getCurrentMin() + random.nextDouble() * (delayRange.getCurrentMax() - delayRange.getCurrentMin()));
    }

    private float getAngleToEntity(Entity entity) {
        Box box = entity.getBoundingBox();
        double dx = box.getCenter().x - mc.player.getX();
        double dz = box.getCenter().z - mc.player.getZ();
        float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0 / Math.PI - 90.0);
        return Math.abs(MathHelper.wrapDegrees(yaw - mc.player.getYaw()));
    }

    public static boolean hasLineOfSight(Vec3d to) {
        return raycast(getEyesPos(), to).getType() == HitResult.Type.MISS;
    }

    public static Vec3d getEyesPos() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        float eyeHeight = player.getEyeHeight(player.getPose());
        return player.getPos().add(0, eyeHeight, 0);
    }

    public static BlockHitResult raycast(Vec3d from, Vec3d to) {
        return raycast(from, to, RaycastContext.FluidHandling.NONE);
    }

    public static BlockHitResult raycast(Vec3d from, Vec3d to, RaycastContext.FluidHandling fluidHandling) {
        RaycastContext context = new RaycastContext(from, to, RaycastContext.ShapeType.COLLIDER, fluidHandling, MinecraftClient.getInstance().player);
        return MinecraftClient.getInstance().world.raycast(context);
    }
}
