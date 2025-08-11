package com.h1ggsk.radon.module.modules.donut;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import com.h1ggsk.radon.Radon;
import com.h1ggsk.radon.module.modules.misc.AutoEat;
import com.h1ggsk.radon.module.modules.combat.AutoTotem;
import com.h1ggsk.radon.utils.embed.DiscordWebhook;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.StartTickEvent;
import com.h1ggsk.radon.mixin.MobSpawnerLogicAccessor;
import com.h1ggsk.radon.module.Category;
import com.h1ggsk.radon.module.Module;
import com.h1ggsk.radon.module.setting.BooleanSetting;
import com.h1ggsk.radon.module.setting.NumberSetting;
import com.h1ggsk.radon.module.setting.StringSetting;
import com.h1ggsk.radon.utils.BlockUtil;
import com.h1ggsk.radon.utils.EnchantmentUtil;
import com.h1ggsk.radon.utils.EncryptedString;
import com.h1ggsk.radon.utils.InventoryUtil;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public final class TunnelBaseFinder extends Module {
    private final NumberSetting minimumStorage = new NumberSetting(EncryptedString.of("Minimum Storage"), 1.0, 500.0, 100.0, 1.0);
    private final BooleanSetting spawners = new BooleanSetting(EncryptedString.of("Spawners"), true);
    private final BooleanSetting autoTotemBuy = new BooleanSetting(EncryptedString.of("Auto Totem Buy"), true);
    private final NumberSetting totemSlot = new NumberSetting(EncryptedString.of("Totem Slot"), 1.0, 9.0, 8.0, 1.0);
    private final BooleanSetting autoMend = new BooleanSetting(EncryptedString.of("Auto Mend"), true).setDescription(EncryptedString.of("Automatically repairs pickaxe."));
    private final NumberSetting xpBottleSlot = new NumberSetting(EncryptedString.of("XP Bottle Slot"), 1.0, 9.0, 9.0, 1.0);
    private final BooleanSetting discordNotification = new BooleanSetting(EncryptedString.of("Discord Notification"), false);
    private final StringSetting webhook = new StringSetting(EncryptedString.of("Webhook"), "");
    private final BooleanSetting totemCheck = new BooleanSetting(EncryptedString.of("Totem Check"), true);
    private final NumberSetting totemCheckTime = new NumberSetting(EncryptedString.of("Totem Check Time"), 1.0, 120.0, 20.0, 1.0);
    private Direction currentDirection;
    private int blocksMined;
    private int spawnerCount;
    private int idleTicks;
    private Vec3d lastPosition;
    private boolean isDigging = false;
    private boolean shouldDig = false;
    private int totemCheckCounter = 0;
    private int totemBuyCounter = 0;
    private double actionDelay = 0.0;

    public TunnelBaseFinder() {
        super(EncryptedString.of("Tunnel Base Finder"), EncryptedString.of("Finds bases digging tunnel"), -1, Category.DONUT);
        this.addSettings(this.minimumStorage, this.spawners, this.autoTotemBuy, this.totemSlot, this.autoMend, this.xpBottleSlot, this.discordNotification, this.webhook, this.totemCheck, this.totemCheckTime);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.currentDirection = this.getInitialDirection();
        this.blocksMined = 0;
        this.idleTicks = 0;
        this.spawnerCount = 0;
        this.lastPosition = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.forwardKey.setPressed(false);
    }

    @EventListener
    public void onTick(final StartTickEvent event) {
        if (this.currentDirection == null) {
            return;
        }
        final Module moduleByClass = Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(AutoEat.class);
        if (moduleByClass.isEnabled() && ((AutoEat) moduleByClass).shouldEat()) {
            return;
        }
        final int n = (this.calculateDirection(this.currentDirection) + 90 * this.idleTicks) % 360;
        if (mc.player.getYaw() != n) {
            mc.player.setYaw((float) n);
        }
        if (mc.player.getPitch() != 2.0f) {
            mc.player.setPitch(2.0f);
        }
        this.updateDirection(this.getInitialDirection());
        if (this.blocksMined > 0) {
            mc.options.forwardKey.setPressed(false);
            --this.blocksMined;
            return;
        }
        this.notifyFound();
        if (this.autoTotemBuy.getValue()) {
            final int n2 = this.totemSlot.getIntValue() - 1;
            if (!mc.player.getInventory().getStack(n2).isOf(Items.TOTEM_OF_UNDYING)) {
                if (this.totemBuyCounter < 30 && !this.shouldDig) {
                    ++this.totemBuyCounter;
                    return;
                }
                this.totemBuyCounter = 0;
                this.shouldDig = true;
                if (mc.player.getInventory().getSelectedSlot() != n2) {
                    InventoryUtil.swap(n2);
                }
                final ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
                if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) || ((GenericContainerScreenHandler) currentScreenHandler).getRows() != 3) {
                    mc.getNetworkHandler().sendChatCommand("shop");
                    this.blocksMined = 10;
                    return;
                }
                if (currentScreenHandler.getSlot(11).getStack().isOf(Items.END_STONE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 13, 0, SlotActionType.PICKUP, mc.player);
                    this.blocksMined = 10;
                    return;
                }
                if (currentScreenHandler.getSlot(16).getStack().isOf(Items.EXPERIENCE_BOTTLE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 13, 0, SlotActionType.PICKUP, mc.player);
                    this.blocksMined = 10;
                    return;
                }
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                if (currentScreenHandler.getSlot(23).getStack().isOf(Items.LIME_STAINED_GLASS_PANE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 23, 0, SlotActionType.PICKUP, mc.player);
                    this.blocksMined = 10;
                    return;
                }
                mc.getNetworkHandler().sendChatCommand("shop");
                this.blocksMined = 10;
                return;
            } else if (this.shouldDig) {
                if (mc.currentScreen != null) {
                    mc.player.closeHandledScreen();
                    this.blocksMined = 20;
                }
                this.shouldDig = false;
                this.totemBuyCounter = 0;
            }
        }
        if (this.isDigging) {
            final int n3 = this.xpBottleSlot.getIntValue() - 1;
            final ItemStack getStack = mc.player.getInventory().getStack(n3);
            if (mc.player.getInventory().getSelectedSlot() != n3) {
                InventoryUtil.swap(n3);
            }
            if (!getStack.isOf(Items.EXPERIENCE_BOTTLE)) {
                final ScreenHandler fishHook = mc.player.currentScreenHandler;
                if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) || ((GenericContainerScreenHandler) fishHook).getRows() != 3) {
                    mc.getNetworkHandler().sendChatCommand("shop");
                    this.blocksMined = 10;
                    return;
                }
                if (fishHook.getSlot(11).getStack().isOf(Items.END_STONE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 13, 0, SlotActionType.PICKUP, mc.player);
                    this.blocksMined = 10;
                    return;
                }
                if (fishHook.getSlot(16).getStack().isOf(Items.EXPERIENCE_BOTTLE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 16, 0, SlotActionType.PICKUP, mc.player);
                    this.blocksMined = 10;
                    return;
                }
                if (fishHook.getSlot(17).getStack().isOf(Items.LIME_STAINED_GLASS_PANE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 17, 0, SlotActionType.PICKUP, mc.player);
                    this.blocksMined = 10;
                    return;
                }
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, net.minecraft.util.math.Direction.DOWN));
                if (fishHook.getSlot(23).getStack().isOf(Items.LIME_STAINED_GLASS_PANE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 23, 0, SlotActionType.PICKUP, mc.player);
                    this.blocksMined = 10;
                    return;
                }
                mc.getNetworkHandler().sendChatCommand("shop");
                this.blocksMined = 10;
            } else {
                if (mc.currentScreen != null) {
                    mc.player.closeHandledScreen();
                    this.blocksMined = 20;
                    return;
                }
                if (!EnchantmentUtil.hasEnchantment(mc.player.getOffHandStack(), Enchantments.MENDING)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + this.totemCheckCounter, 40, SlotActionType.SWAP, mc.player);
                    this.blocksMined = 20;
                    return;
                }
                if (mc.player.getOffHandStack().getDamage() > 0) {
                    final ActionResult interactItem = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    if (interactItem.isAccepted()) {
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    this.blocksMined = 1;
                    return;
                }
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + this.totemCheckCounter, 40, SlotActionType.SWAP, mc.player);
                this.isDigging = false;
            }
        } else {
            if (this.autoMend.getValue()) {
                final ItemStack size = mc.player.getMainHandStack();
                if (EnchantmentUtil.hasEnchantment(size, Enchantments.MENDING) && size.getMaxDamage() - size.getDamage() < 100) {
                    this.isDigging = true;
                    this.totemCheckCounter = mc.player.getInventory().getSelectedSlot();
                }
            }
            if (this.totemCheck.getValue()) {
                final boolean equals = mc.player.getOffHandStack().getItem().equals(Items.TOTEM_OF_UNDYING);
                final Module moduleByClass2 = Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(AutoTotem.class);
                if (equals) {
                    this.actionDelay = 0.0;
                } else if (moduleByClass2.isEnabled() && ((AutoTotem) moduleByClass2).findItemSlot(Items.TOTEM_OF_UNDYING) != -1) {
                    this.actionDelay = 0.0;
                } else {
                    ++this.actionDelay;
                }
                if (this.actionDelay > this.totemCheckTime.getValue()) {
                    this.notifyTotemExploded("Your totem exploded", (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
                    return;
                }
            }
            boolean a = false;
            final HitResult crosshairTarget = mc.crosshairTarget;
            if (mc.crosshairTarget instanceof BlockHitResult) {
                final BlockPos blockPos = ((BlockHitResult) crosshairTarget).getBlockPos();
                if (!BlockUtil.isBlockAtPosition(blockPos, Blocks.AIR)) {
                    a = this.isBlockPositionValid(blockPos, mc.player.getHorizontalFacing());
                }
            }
            if (a) {
                this.handleBlockBreaking(true);
            }
            final boolean a2 = this.isBlockInDirection(mc.player.getHorizontalFacing(), 3);
            boolean b = false;
            final HitResult crosshairTarget2 = mc.crosshairTarget;
            if (mc.crosshairTarget instanceof BlockHitResult) {
                b = (mc.player.getCameraPosVec(1.0f).distanceTo(Vec3d.ofCenter(((BlockHitResult) crosshairTarget2).getBlockPos())) > 3.0);
            }
            if (!a && (!b || !a2)) {
                ++this.idleTicks;
                this.lastPosition = mc.player.getPos();
                this.blocksMined = 5;
                return;
            }
            mc.options.forwardKey.setPressed(a2 && b);
            if (this.idleTicks > 0 && this.lastPosition != null && mc.player.getPos().distanceTo(this.lastPosition) > 1.0) {
                this.lastPosition = mc.player.getPos();
                final net.minecraft.util.math.Direction rotateYCounterclockwise = mc.player.getHorizontalFacing().rotateYCounterclockwise();
                BlockPos blockPos2 = mc.player.getBlockPos().up().offset(rotateYCounterclockwise);
                for (int i = 0; i < 5; ++i) {
                    blockPos2 = blockPos2.offset(rotateYCounterclockwise);
                    if (!mc.world.getBlockState(blockPos2).getBlock().equals(Blocks.AIR)) {
                        if (this.isBlockPositionValid(blockPos2, rotateYCounterclockwise) && this.isBlockPositionValid(blockPos2.offset(rotateYCounterclockwise), rotateYCounterclockwise)) {
                            --this.idleTicks;
                            this.blocksMined = 5;
                        }
                        return;
                    }
                }
            }
        }
    }

    private int calculateDirection(final Direction enum4) {
        if (enum4 == Direction.NORTH) {
            return 180;
        }
        if (enum4 == Direction.SOUTH) {
            return 0;
        }
        if (enum4 == Direction.EAST) {
            return 270;
        }
        if (enum4 == Direction.WEST) {
            return 90;
        }
        return Math.round(mc.player.getYaw());
    }

    private boolean isBlockInDirection(final net.minecraft.util.math.Direction direction, final int n) {
        final BlockPos down = mc.player.getBlockPos().down();
        final BlockPos getBlockPos = mc.player.getBlockPos();
        for (int i = 0; i < n; ++i) {
            final BlockPos offset = down.offset(direction, i);
            final BlockPos offset2 = getBlockPos.offset(direction, i);
            if (mc.world.getBlockState(offset).isAir() || !this.isBlockPositionSafe(offset)) {
                return false;
            }
            if (!mc.world.getBlockState(offset2).isAir()) {
                return false;
            }
        }
        return true;
    }

    private boolean isBlockPositionValid(final BlockPos blockPos, final net.minecraft.util.math.Direction direction) {
        final BlockPos offset = blockPos.offset(direction);
        final net.minecraft.util.math.Direction rotateYClockwise = direction.rotateYClockwise();
        final net.minecraft.util.math.Direction up = net.minecraft.util.math.Direction.UP;
        final BlockPos offset2 = blockPos.offset(net.minecraft.util.math.Direction.UP, 2);
        final BlockPos offset3 = blockPos.offset(net.minecraft.util.math.Direction.DOWN, -2);
        final BlockPos offset4 = offset2.offset(rotateYClockwise, -1);
        if (!this.isBlockPositionSafe(offset4) || mc.world.getBlockState(offset4).getBlock() == Blocks.GRAVEL) {
            return false;
        }
        if (!this.isBlockPositionSafe(offset3.offset(rotateYClockwise, -1))) {
            return false;
        }
        final BlockPos offset5 = blockPos.offset(rotateYClockwise, 2);
        final BlockPos offset6 = blockPos.offset(rotateYClockwise, -2);
        if (!this.isBlockPositionSafe(offset5.offset(up, -1))) {
            return false;
        }
        if (!this.isBlockPositionSafe(offset6.offset(up, -1))) {
            return false;
        }
        while (this.isBlockPositionSafe(offset.offset(rotateYClockwise, -1).offset(up, -1))) {
        }
        return false;
    }

    private boolean isBlockPositionSafe(final BlockPos blockPos) {
        return this.isBlockValid(mc.world.getBlockState(blockPos).getBlock());
    }

    private boolean isBlockValid(final Block block) {
        return block != Blocks.LAVA && block != Blocks.WATER;
    }

    private void updateDirection(final Direction enum4) {
        final double getX = mc.player.getX();
        final double getY = mc.player.getZ();
        final double floor = Math.floor(getY);
        final double n = Math.floor(getX) + 0.5 - getX;
        final double n2 = floor + 0.5 - getY;
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        boolean b = false;
        boolean b2 = false;
        if (enum4 == Direction.SOUTH) {
            if (n > 0.1) {
                b2 = true;
            } else if (n < -0.1) {
                b = true;
            }
        }
        if (enum4 == Direction.NORTH) {
            if (n > 0.1) {
                b = true;
            } else if (n < -0.1) {
                b2 = true;
            }
        }
        if (enum4 == Direction.WEST) {
            if (n2 > 0.1) {
                b2 = true;
            } else if (n2 < -0.1) {
                b = true;
            }
        }
        if (enum4 == Direction.EAST) {
            if (n2 > 0.1) {
                b = true;
            } else if (n2 < -0.1) {
                b2 = true;
            }
        }
        if (b) {
            mc.options.rightKey.setPressed(true);
        }
        if (b2) {
            mc.options.leftKey.setPressed(true);
        }
    }

    private void handleBlockBreaking(final boolean b) {
        if (!mc.player.isUsingItem()) {
            if (b && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                final BlockHitResult blockHitResult = (BlockHitResult) mc.crosshairTarget;
                final BlockPos blockPos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
                if (!mc.world.getBlockState(blockPos).isAir()) {
                    final net.minecraft.util.math.Direction side = blockHitResult.getSide();
                    if (mc.interactionManager.updateBlockBreakingProgress(blockPos, side)) {
                        mc.particleManager.addBlockBreakingParticles(blockPos, side);
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                }
            } else {
                mc.interactionManager.cancelBlockBreaking();
            }
        }
    }

    private Direction getInitialDirection() {
        float n = mc.player.getYaw() % 360.0f;
        if (n < 0.0f) {
            n += 360.0f;
        }
        if (n >= 45.0f && n < 135.0f) {
            return Direction.WEST;
        }
        if (n >= 135.0f && n < 225.0f) {
            return Direction.NORTH;
        }
        if (n >= 225.0f && n < 315.0f) {
            return Direction.EAST;
        }
        return Direction.SOUTH;
    }

    private void notifyFound() {
        int n = 0;
        int n2 = 0;
        BlockPos blockPos = null;
        final Iterator iterator = BlockUtil.getLoadedChunks().iterator();
        while (iterator.hasNext()) {
            for (final Object next : ((WorldChunk) iterator.next()).getBlockEntityPositions()) {
                final BlockEntity getBlockEntity = mc.world.getBlockEntity((BlockPos) next);
                if (this.spawners.getValue() && getBlockEntity instanceof MobSpawnerBlockEntity) {
                    final String string = ((MobSpawnerLogicAccessor) ((MobSpawnerBlockEntity) getBlockEntity).getLogic()).getSpawnEntry().getNbt().getString("id").toString();
                    if (string != "minecraft:cave_spider" && string != "minecraft:spider") {
                        ++n2;
                        blockPos = (BlockPos) next;
                    }
                }
                if (!(getBlockEntity instanceof ChestBlockEntity) && !(getBlockEntity instanceof EnderChestBlockEntity)) {
                    if (getBlockEntity instanceof ShulkerBoxBlockEntity) {
                        throw new RuntimeException();
                    }
                    if (!(getBlockEntity instanceof FurnaceBlockEntity) && !(getBlockEntity instanceof BarrelBlockEntity) && !(getBlockEntity instanceof EnchantingTableBlockEntity)) {
                        continue;
                    }
                }
                ++n;
            }
        }
        if (n2 > 0) {
            ++this.spawnerCount;
        } else {
            this.spawnerCount = 0;
        }
        if (this.spawnerCount > 10) {
            this.notifyFound("YOU FOUND SPAWNER", blockPos.getX(), blockPos.getY(), blockPos.getZ(), false);
            this.spawnerCount = 0;
        }
        if (n > this.minimumStorage.getIntValue()) {
            this.notifyFound("YOU FOUND BASE", (int) mc.player.getPos().x, (int) mc.player.getPos().y, (int) mc.player.getPos().z, true);
        }
    }

    private void notifyTotemExploded(final String s, final int n, final int n2, final int n3) {
        if (this.discordNotification.getValue()) {
            final DiscordWebhook embedSender = new DiscordWebhook(this.webhook.value);
            final DiscordWebhook.EmbedObject bn = new DiscordWebhook.EmbedObject();
            bn.setTitle("Totem Exploded");
            bn.setThumbnail("https://render.crafty.gg/3d/bust/" + MinecraftClient.getInstance().getSession().getUuidOrNull() + "?format=webp");
            bn.setDescription("Your Totem Exploded - " + MinecraftClient.getInstance().getSession().getUsername());
            bn.setColor(Color.RED);
            bn.setFooter(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), null);
            bn.addField("Location", "x: " + n + " y: " + n2 + " z: " + n3, true);
            embedSender.addEmbed(bn);
            try {
                embedSender.execute();
            } catch (final Throwable ex) {
            }
        }
        this.disconnectWithMessage(Text.of(s));
    }

    private void notifyFound(final String s, final int n, final int n2, final int n3, final boolean b) {
        String s2;
        if (b) {
            s2 = "Base";
        } else {
            s2 = "Spawner";
        }
        if (this.discordNotification.getValue()) {
            final DiscordWebhook embedSender = new DiscordWebhook(this.webhook.value);
            final DiscordWebhook.EmbedObject bn = new DiscordWebhook.EmbedObject();
            bn.setTitle(s2);
            bn.setThumbnail("https://render.crafty.gg/3d/bust/" + MinecraftClient.getInstance().getSession().getUuidOrNull() + "?format=webp");
            bn.setDescription(s2 + " Found - " + MinecraftClient.getInstance().getSession().getUsername());
            bn.setColor(Color.GRAY);
            bn.setFooter(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), null);
            bn.addField(s2 + "Found at", "x: " + n + " y: " + n2 + " z: " + n3, true);
            embedSender.addEmbed(bn);
            try {
                embedSender.execute();
            } catch (final Throwable ex) {
            }
        }
        this.toggle();
        this.disconnectWithMessage(Text.of(s));
    }

    private void disconnectWithMessage(final Text text) {
        final MutableText literal = Text.literal("[TunnelBaseFinder] ");
        literal.append(text);
        this.toggle();
        mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(literal));
    }

    public boolean isDigging() {
        return this.isDigging;
    }

    enum Direction {
        NORTH("north", 0),
        SOUTH("south", 1),
        EAST("east", 2),
        WEST("west", 3);

        Direction(final String name, final int ordinal) {
        }
    }

}
