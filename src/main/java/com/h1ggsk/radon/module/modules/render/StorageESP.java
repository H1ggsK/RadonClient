package com.h1ggsk.radon.module.modules.render;

import net.minecraft.block.entity.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.Render3DEvent;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.utils.BlockUtil;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.RenderUtils;

import java.awt.*;

public final class StorageESP extends Module {
    private final NumberSetting alpha = new NumberSetting(EncryptedString.of("Alpha"), 1.0, 255.0, 125.0, 1.0);
    private final BooleanSetting tracers = new BooleanSetting(EncryptedString.of("Tracers"), false).setDescription(EncryptedString.of("Draws keybindListener line from your player to the storage block"));
    private final BooleanSetting chests = new BooleanSetting(EncryptedString.of("Chests"), true);
    private final BooleanSetting enderChests = new BooleanSetting(EncryptedString.of("Ender Chests"), true);
    private final BooleanSetting spawners = new BooleanSetting(EncryptedString.of("Spawners"), true);
    private final BooleanSetting shulkerBoxes = new BooleanSetting(EncryptedString.of("Shulker Boxes"), true);
    private final BooleanSetting furnaces = new BooleanSetting(EncryptedString.of("Furnaces"), true);
    private final BooleanSetting barrels = new BooleanSetting(EncryptedString.of("Barrels"), true);
    private final BooleanSetting enchant = new BooleanSetting(EncryptedString.of("Enchanting Tables"), true);
    private final BooleanSetting pistons = new BooleanSetting(EncryptedString.of("Pistons"), true);

    public StorageESP() {
        super(EncryptedString.of("Storage ESP"), EncryptedString.of("Renders storage blocks through walls"), -1, Category.RENDER);
        this.addSettings(this.alpha, this.tracers, this.chests, this.enderChests, this.spawners, this.shulkerBoxes, this.furnaces, this.barrels, this.enchant, this.pistons);
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
        this.renderStorages(render3DEvent);
    }

    private Color getBlockEntityColor(final BlockEntity blockEntity, final int a) {
        if (blockEntity instanceof TrappedChestBlockEntity && this.chests.getValue()) {
            return new Color(200, 91, 0, a);
        }
        if (blockEntity instanceof ChestBlockEntity && this.chests.getValue()) {
            return new Color(156, 91, 0, a);
        }
        if (blockEntity instanceof EnderChestBlockEntity && this.enderChests.getValue()) {
            return new Color(117, 0, 255, a);
        }
        if (blockEntity instanceof MobSpawnerBlockEntity && this.spawners.getValue()) {
            return new Color(138, 126, 166, a);
        }
        if (blockEntity instanceof ShulkerBoxBlockEntity && this.shulkerBoxes.getValue()) {
            return new Color(134, 0, 158, a);
        }
        if (blockEntity instanceof FurnaceBlockEntity && this.furnaces.getValue()) {
            return new Color(125, 125, 125, a);
        }
        if (blockEntity instanceof BarrelBlockEntity && this.barrels.getValue()) {
            return new Color(255, 140, 140, a);
        }
        if (blockEntity instanceof EnchantingTableBlockEntity && this.enchant.getValue()) {
            return new Color(80, 80, 255, a);
        }
        if (blockEntity instanceof PistonBlockEntity && this.pistons.getValue()) {
            return new Color(35, 226, 0, a);
        }
        return new Color(255, 255, 255, 0);
    }

    private void renderStorages(Render3DEvent event) {
        Camera cam = RenderUtils.getCamera();
        if (cam != null) {
            Vec3d camPos = RenderUtils.getCameraPos();
            MatrixStack matrices = event.matrixStack;
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cam.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cam.getYaw() + 180.0f));
            matrices.translate(-camPos.x, -camPos.y, -camPos.z);
        }

        for (WorldChunk chunk : BlockUtil.getLoadedChunks().toList()) {
            for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
                BlockEntity blockEntity = mc.world.getBlockEntity(blockPos);

                RenderUtils.renderFilledBox(event.matrixStack, blockPos.getX() + 0.1F, blockPos.getY() + 0.05F, blockPos.getZ() + 0.1F, blockPos.getX() + 0.9F, blockPos.getY() + 0.85F, blockPos.getZ() + 0.9F, getBlockEntityColor(blockEntity, alpha.getIntValue()));

                if (tracers.getValue())
                    RenderUtils.renderLine(event.matrixStack, getBlockEntityColor(blockEntity, 255), mc.crosshairTarget.getPos(), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5));
            }
        }

        MatrixStack matrixStack = event.matrixStack;
        matrixStack.pop();
    }
}
