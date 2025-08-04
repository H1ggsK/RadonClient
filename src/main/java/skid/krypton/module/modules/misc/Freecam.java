package skid.krypton.module.modules.misc;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import skid.krypton.event.EventListener;
import skid.krypton.event.events.*;
import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.module.setting.NumberSetting;
import skid.krypton.utils.EncryptedString;
import skid.krypton.utils.KeyUtils;
import skid.krypton.utils.Utils;

public final class Freecam extends Module {
    private final NumberSetting speed = new NumberSetting(EncryptedString.of("Speed"), 1.0, 10.0, 1.0, 0.1);
    public final Vector3d currentPosition = new Vector3d();
    public final Vector3d previousPosition = new Vector3d();
    private Perspective currentPerspective;
    private double movementSpeed;
    public float yaw;
    public float pitch;
    public float previousYaw;
    public float previousPitch;
    private boolean isMovingForward;
    private boolean isMovingBackward;
    private boolean isMovingRight;
    private boolean isMovingLeft;
    private boolean isMovingUp;
    private boolean isMovingDown;

    public Freecam() {
        super(EncryptedString.of("Freecam"), EncryptedString.of("Lets you move freely around the world without actually moving"), -1, Category.MISC);
        this.addSettings(this.speed);
    }

    @Override
    public void onEnable() {
        if (mc == null) return;
        if (mc.player == null) {
            this.toggle();
            return;
        }
        mc.options.getFovEffectScale().setValue(0.0);
        mc.options.getBobView().setValue(false);
        this.yaw = mc.player.getYaw();
        this.pitch = mc.player.getPitch();
        this.currentPerspective = mc.options.getPerspective();
        this.movementSpeed = this.speed.getValue();
        Utils.copyVector(this.currentPosition, mc.gameRenderer.getCamera().getPos());
        Utils.copyVector(this.previousPosition, mc.gameRenderer.getCamera().getPos());
        if (mc.options.getPerspective() == Perspective.THIRD_PERSON_FRONT) {
            this.yaw += 180.0f;
            this.pitch *= -1.0f;
        }
        this.previousYaw = this.yaw;
        this.previousPitch = this.pitch;
        this.isMovingForward = mc.options.forwardKey.isPressed();
        this.isMovingBackward = mc.options.backKey.isPressed();
        this.isMovingRight = mc.options.rightKey.isPressed();
        this.isMovingLeft = mc.options.leftKey.isPressed();
        this.isMovingUp = mc.options.jumpKey.isPressed();
        this.isMovingDown = mc.options.sneakKey.isPressed();
        this.resetMovementKeys();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.resetMovementKeys();
        this.previousPosition.set(this.currentPosition);
        this.previousYaw = this.yaw;
        this.previousPitch = this.pitch;
        super.onDisable();
    }

    private void resetMovementKeys() {
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.sneakKey.setPressed(false);
    }

    @EventListener
    private void handleSetScreenEvent(final SetScreenEvent setScreenEvent) {
        this.resetMovementKeys();
        this.previousPosition.set(this.currentPosition);
        this.previousYaw = this.yaw;
        this.previousPitch = this.pitch;
    }

    @EventListener
    private void handleTickEvent(final StartTickEvent startTickEvent) {
        if (skid.krypton.Krypton.mc.player == null || skid.krypton.Krypton.mc.currentScreen instanceof ChatScreen || skid.krypton.Krypton.mc.currentScreen instanceof SignEditScreen || skid.krypton.Krypton.mc.currentScreen instanceof HangingSignEditScreen || skid.krypton.Krypton.mc.currentScreen instanceof AnvilScreen) {
            return;
        }
        if (mc.cameraEntity.isInsideWall()) {
            mc.getCameraEntity().noClip = true;
        }
        if (!this.currentPerspective.isFirstPerson()) {
            mc.options.setPerspective(Perspective.FIRST_PERSON);
        }
        final Vec3d forwardVector = Vec3d.fromPolar(0.0f, this.yaw);
        final Vec3d rightVector = Vec3d.fromPolar(0.0f, this.yaw + 90.0f);
        double xMovement = 0.0;
        double yMovement = 0.0;
        double zMovement = 0.0;
        double speedMultiplier = 0.5;
        if (mc.options.sprintKey.isPressed()) {
            speedMultiplier = 1.0;
        }
        boolean isMovingHorizontally = false;
        if (this.isMovingForward) {
            xMovement += forwardVector.x * speedMultiplier * this.movementSpeed;
            zMovement += forwardVector.z * speedMultiplier * this.movementSpeed;
            isMovingHorizontally = true;
        }
        if (this.isMovingBackward) {
            xMovement -= forwardVector.x * speedMultiplier * this.movementSpeed;
            zMovement -= forwardVector.z * speedMultiplier * this.movementSpeed;
            isMovingHorizontally = true;
        }
        boolean isMovingLaterally = false;
        if (this.isMovingRight) {
            xMovement += rightVector.x * speedMultiplier * this.movementSpeed;
            zMovement += rightVector.z * speedMultiplier * this.movementSpeed;
            isMovingLaterally = true;
        }
        if (this.isMovingLeft) {
            xMovement -= rightVector.x * speedMultiplier * this.movementSpeed;
            zMovement -= rightVector.z * speedMultiplier * this.movementSpeed;
            isMovingLaterally = true;
        }
        if (isMovingHorizontally && isMovingLaterally) {
            final double diagonalMultiplier = 1.0 / Math.sqrt(2.0);
            xMovement *= diagonalMultiplier;
            zMovement *= diagonalMultiplier;
        }
        if (this.isMovingUp) {
            yMovement += speedMultiplier * this.movementSpeed;
        }
        if (this.isMovingDown) {
            yMovement -= speedMultiplier * this.movementSpeed;
        }
        this.previousPosition.set(this.currentPosition);
        this.currentPosition.set(this.currentPosition.x + xMovement, this.currentPosition.y + yMovement, this.currentPosition.z + zMovement);
    }

    @EventListener
    public void onKey(final KeyEvent keyEvent) {
        if (skid.krypton.Krypton.mc.player == null || skid.krypton.Krypton.mc.currentScreen instanceof ChatScreen || skid.krypton.Krypton.mc.currentScreen instanceof SignEditScreen || skid.krypton.Krypton.mc.currentScreen instanceof HangingSignEditScreen || skid.krypton.Krypton.mc.currentScreen instanceof AnvilScreen) {
            return;
        }
        if (KeyUtils.isKeyPressed(292)) {
            return;
        }
        boolean keyHandled = true;
        if (mc.options.forwardKey.matchesKey(keyEvent.key, 0)) {
            this.isMovingForward = (keyEvent.mode != 0);
            mc.options.forwardKey.setPressed(false);
        } else if (mc.options.backKey.matchesKey(keyEvent.key, 0)) {
            this.isMovingBackward = (keyEvent.mode != 0);
            mc.options.backKey.setPressed(false);
        } else if (mc.options.rightKey.matchesKey(keyEvent.key, 0)) {
            this.isMovingRight = (keyEvent.mode != 0);
            mc.options.rightKey.setPressed(false);
        } else if (mc.options.leftKey.matchesKey(keyEvent.key, 0)) {
            this.isMovingLeft = (keyEvent.mode != 0);
            mc.options.leftKey.setPressed(false);
        } else if (mc.options.jumpKey.matchesKey(keyEvent.key, 0)) {
            this.isMovingUp = (keyEvent.mode != 0);
            mc.options.jumpKey.setPressed(false);
        } else if (mc.options.sneakKey.matchesKey(keyEvent.key, 0)) {
            this.isMovingDown = (keyEvent.mode != 0);
            mc.options.sneakKey.setPressed(false);
        } else {
            keyHandled = false;
        }
        if (keyHandled) {
            keyEvent.cancel();
        }
    }

    @EventListener
    private void handleMouseButtonEvent(final MouseButtonEvent event) {
        boolean buttonHandled = true;
        if (mc.options.forwardKey.matchesMouse(event.button)) {
            this.isMovingForward = (event.actions != 0);
            mc.options.forwardKey.setPressed(false);
        } else if (mc.options.backKey.matchesMouse(event.button)) {
            this.isMovingBackward = (event.actions != 0);
            mc.options.backKey.setPressed(false);
        } else if (mc.options.rightKey.matchesMouse(event.button)) {
            this.isMovingRight = (event.actions != 0);
            mc.options.rightKey.setPressed(false);
        } else if (mc.options.leftKey.matchesMouse(event.button)) {
            this.isMovingLeft = (event.actions != 0);
            mc.options.leftKey.setPressed(false);
        } else if (mc.options.jumpKey.matchesMouse(event.button)) {
            this.isMovingUp = (event.actions != 0);
            mc.options.jumpKey.setPressed(false);
        } else if (mc.options.sneakKey.matchesMouse(event.button)) {
            this.isMovingDown = (event.actions != 0);
            mc.options.sneakKey.setPressed(false);
        } else {
            buttonHandled = false;
        }
        if (buttonHandled) {
            event.cancel();
        }
    }

    @EventListener
    private void handleMouseScrolledEvent(final MouseScrolledEvent mouseScrolledEvent) {
        if (mc.currentScreen == null) {
            this.movementSpeed += mouseScrolledEvent.amount * 0.25 * this.movementSpeed;
            if (this.movementSpeed < 0.1) {
                this.movementSpeed = 0.1;
            }
            mouseScrolledEvent.cancel();
        }
    }

    @EventListener
    private void handleChunkMarkClosedEvent(final ChunkMarkClosedEvent chunkMarkClosedEvent) {
        chunkMarkClosedEvent.cancel();
    }

    public void updateRotation(final double deltaYaw, final double deltaPitch) {
        this.previousYaw = this.yaw;
        this.previousPitch = this.pitch;
        this.yaw += (float) deltaYaw;
        this.pitch += (float) deltaPitch;
        this.pitch = MathHelper.clamp(this.pitch, -90.0f, 90.0f);
    }

    public double getInterpolatedX(final float partialTicks) {
        return MathHelper.lerp(partialTicks, this.previousPosition.x, this.currentPosition.x);
    }

    public double getInterpolatedY(final float partialTicks) {
        return MathHelper.lerp(partialTicks, this.previousPosition.y, this.currentPosition.y);
    }

    public double getInterpolatedZ(final float partialTicks) {
        return MathHelper.lerp(partialTicks, this.previousPosition.z, this.currentPosition.z);
    }

    public double getInterpolatedYaw(final float partialTicks) {
        return MathHelper.lerp(partialTicks, this.previousYaw, this.yaw);
    }

    public double getInterpolatedPitch(final float partialTicks) {
        return MathHelper.lerp(partialTicks, this.previousPitch, this.pitch);
    }
}