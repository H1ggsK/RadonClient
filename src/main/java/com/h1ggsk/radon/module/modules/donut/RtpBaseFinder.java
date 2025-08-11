package com.h1ggsk.radon.module.modules.donut;

import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
import net.minecraft.util.math.Direction;
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
import com.h1ggsk.radon.module.setting.ModeSetting;
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
import java.util.Random;

public final class RtpBaseFinder extends Module {
    public final ModeSetting<Mode> mode = new ModeSetting<>(EncryptedString.of("Mode"), Mode.RANDOM, Mode.class);
    private final BooleanSetting spawn = new BooleanSetting(EncryptedString.of("Spawners"), true);
    private final NumberSetting minStorage = new NumberSetting(EncryptedString.of("Minimum Storage"), 1.0, 500.0, 100.0, 1.0);
    private final BooleanSetting autoTotemBuy = new BooleanSetting(EncryptedString.of("Auto Totem Buy"), true);
    private final NumberSetting totemSlot = new NumberSetting(EncryptedString.of("Totem Slot"), 1.0, 9.0, 8.0, 1.0);
    private final BooleanSetting autoMend = new BooleanSetting(EncryptedString.of("Auto Mend"), true).setDescription(EncryptedString.of("Automatically repairs pickaxe."));
    private final NumberSetting xpBottleSlot = new NumberSetting(EncryptedString.of("XP Bottle Slot"), 1.0, 9.0, 9.0, 1.0);
    private final BooleanSetting discordNotification = new BooleanSetting(EncryptedString.of("Discord Notification"), false);
    private final StringSetting webhook = new StringSetting(EncryptedString.of("Webhook"), "");
    private final BooleanSetting totemCheck = new BooleanSetting(EncryptedString.of("Totem Check"), true);
    private final NumberSetting totemCheckTime = new NumberSetting(EncryptedString.of("Totem Check Time"), 1.0, 120.0, 20.0, 1.0);
    private final NumberSetting digToY = new NumberSetting(EncryptedString.of("Dig To Y"), -59.0, 30.0, -20.0, 1.0);
    private Vec3d currentPosition;
    private Vec3d previousPosition;
    private double idleTime;
    private double totemCheckCounter = 0.0;
    private boolean isDigging = false;
    private boolean shouldDig = false;
    private boolean isRepairing = false;
    private boolean isBuyingTotem = false;
    private int selectedSlot = 0;
    private int rtpCooldown = 0;
    private int actionDelay = 0;
    private int totemBuyCounter = 0;
    private int spawnerCounter = 0;

    public RtpBaseFinder() {
        super(EncryptedString.of("RTPBaseFinder"), EncryptedString.of("Automatically searches for bases on DonutSMP"), -1, Category.DONUT);
        this.addSettings(this.mode, this.spawn, this.minStorage, this.autoTotemBuy, this.totemSlot, this.autoMend, this.xpBottleSlot, this.discordNotification, this.webhook, this.totemCheck, this.totemCheckTime, this.digToY);
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
    public void onTick(final StartTickEvent event) {
        if (mc.player == null) {
            return;
        }
        if (this.actionDelay > 0) {
            --this.actionDelay;
            return;
        }
        this.scanForEntities();
        if (this.autoTotemBuy.getValue()) {
            final int n = this.totemSlot.getIntValue() - 1;
            if (!mc.player.getInventory().getStack(n).isOf(Items.TOTEM_OF_UNDYING)) {
                if (this.totemBuyCounter < 30 && !this.isBuyingTotem) {
                    ++this.totemBuyCounter;
                    return;
                }
                this.totemBuyCounter = 0;
                this.isBuyingTotem = true;
                if (mc.player.getInventory().getSelectedSlot() != n) {
                    InventoryUtil.swap(n);
                }
                final ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
                if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) || ((GenericContainerScreenHandler) currentScreenHandler).getRows() != 3) {
                    mc.getNetworkHandler().sendChatCommand("shop");
                    this.actionDelay = 10;
                    return;
                }
                if (currentScreenHandler.getSlot(11).getStack().isOf(Items.END_STONE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 13, 0, SlotActionType.PICKUP, mc.player);
                    this.actionDelay = 10;
                    return;
                }
                if (currentScreenHandler.getSlot(16).getStack().isOf(Items.EXPERIENCE_BOTTLE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 13, 0, SlotActionType.PICKUP, mc.player);
                    this.actionDelay = 10;
                    return;
                }
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, Direction.DOWN));
                if (currentScreenHandler.getSlot(23).getStack().isOf(Items.LIME_STAINED_GLASS_PANE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 23, 0, SlotActionType.PICKUP, mc.player);
                    this.actionDelay = 10;
                    return;
                }
                mc.getNetworkHandler().sendChatCommand("shop");
                this.actionDelay = 10;
                return;
            } else if (this.isBuyingTotem) {
                if (mc.currentScreen != null) {
                    mc.player.closeHandledScreen();
                    this.actionDelay = 20;
                }
                this.isBuyingTotem = false;
                this.totemBuyCounter = 0;
            }
        }
        if (this.isRepairing) {
            final int n2 = this.xpBottleSlot.getIntValue() - 1;
            final ItemStack getStack = mc.player.getInventory().getStack(n2);
            if (mc.player.getInventory().getSelectedSlot() != n2) {
                InventoryUtil.swap(n2);
            }
            if (!getStack.isOf(Items.EXPERIENCE_BOTTLE)) {
                final ScreenHandler fishHook = mc.player.currentScreenHandler;
                if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) || ((GenericContainerScreenHandler) fishHook).getRows() != 3) {
                    mc.getNetworkHandler().sendChatCommand("shop");
                    this.actionDelay = 10;
                    return;
                }
                if (fishHook.getSlot(11).getStack().isOf(Items.END_STONE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 13, 0, SlotActionType.PICKUP, mc.player);
                    this.actionDelay = 10;
                    return;
                }
                if (fishHook.getSlot(16).getStack().isOf(Items.EXPERIENCE_BOTTLE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 16, 0, SlotActionType.PICKUP, mc.player);
                    this.actionDelay = 10;
                    return;
                }
                if (fishHook.getSlot(17).getStack().isOf(Items.LIME_STAINED_GLASS_PANE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 17, 0, SlotActionType.PICKUP, mc.player);
                    this.actionDelay = 10;
                    return;
                }
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, Direction.DOWN));
                if (fishHook.getSlot(23).getStack().isOf(Items.LIME_STAINED_GLASS_PANE)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 23, 0, SlotActionType.PICKUP, mc.player);
                    this.actionDelay = 10;
                    return;
                }
                mc.getNetworkHandler().sendChatCommand("shop");
                this.actionDelay = 10;
            } else {
                if (mc.currentScreen != null) {
                    mc.player.closeHandledScreen();
                    this.actionDelay = 20;
                    return;
                }
                if (!EnchantmentUtil.hasEnchantment(mc.player.getOffHandStack(), Enchantments.MENDING)) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + this.selectedSlot, 40, SlotActionType.SWAP, mc.player);
                    this.actionDelay = 20;
                    return;
                }
                if (mc.player.getOffHandStack().getDamage() > 0) {
                    final ActionResult interactItem = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    if (interactItem.isAccepted()) {
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    this.actionDelay = 1;
                    return;
                }
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + this.selectedSlot, 40, SlotActionType.SWAP, mc.player);
                this.isRepairing = false;
            }
        } else {
            if (this.shouldDig) {
                this.handleAutoEat();
            }
            if (this.totemCheck.getValue()) {
                final boolean equals = mc.player.getOffHandStack().getItem().equals(Items.TOTEM_OF_UNDYING);
                final Module moduleByClass = Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(AutoTotem.class);
                if (equals) {
                    this.totemCheckCounter = 0.0;
                } else if (moduleByClass.isEnabled() && ((AutoTotem) moduleByClass).findItemSlot(Items.TOTEM_OF_UNDYING) != -1) {
                    this.totemCheckCounter = 0.0;
                } else {
                    ++this.totemCheckCounter;
                }
                if (this.totemCheckCounter > this.totemCheckTime.getValue()) {
                    this.notifyTotemExplosion("Your totem exploded", (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
                    return;
                }
            }
            if (this.rtpCooldown > 0) {
                --this.rtpCooldown;
                if (this.rtpCooldown < 1) {
                    if (this.previousPosition != null && this.previousPosition.distanceTo(mc.player.getPos()) < 100.0) {
                        this.sendRtpCommand();
                        return;
                    }
                    mc.player.setPitch(89.9f);
                    if (this.autoMend.getValue()) {
                        final ItemStack size = mc.player.getMainHandStack();
                        if (EnchantmentUtil.hasEnchantment(size, Enchantments.MENDING) && size.getMaxDamage() - size.getDamage() < 100) {
                            this.isRepairing = true;
                            this.selectedSlot = mc.player.getInventory().getSelectedSlot();
                        }
                    }
                    this.shouldDig = true;
                }
                return;
            }
            if (this.currentPosition != null && this.currentPosition.distanceTo(mc.player.getPos()) < 2.0) {
                ++this.idleTime;
            } else {
                this.currentPosition = mc.player.getPos();
                this.idleTime = 0.0;
            }
            if (this.idleTime > 20.0 && this.isDigging) {
                this.sendRtpCommand();
                this.isDigging = false;
                return;
            }
            if (this.idleTime > 200.0) {
                this.sendRtpCommand();
                this.idleTime = 0.0;
                return;
            }
            if (mc.player.getY() < this.digToY.getIntValue() && !this.isDigging) {
                this.isDigging = true;
                this.shouldDig = false;
            }
        }
    }

    private void sendRtpCommand() {
        this.shouldDig = false;
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        Mode l;
        if (this.mode.getValue() == Mode.RANDOM) {
            l = this.getRandomMode();
        } else {
            l = (Mode) this.mode.getValue();
        }
        networkHandler.sendChatCommand("rtp " + this.getModeName(l));
        this.rtpCooldown = 150;
        this.idleTime = 0.0;
        this.previousPosition = new Vec3d(mc.player.getPos().toVector3f());
    }

    private void disconnectWithMessage(final Text text) {
        final MutableText literal = Text.literal("[RTPBaseFinder] ");
        literal.append(text);
        this.toggle();
        mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(literal));
    }

    private Mode getRandomMode() {
        final Mode[] array = {Mode.EUCENTRAL, Mode.EUWEST, Mode.EAST, Mode.WEST, Mode.ASIA, Mode.OCEANIA};
        return array[new Random().nextInt(array.length)];
    }

    private String getModeName(final Mode mode) {
        final int n = mode.ordinal() ^ 0x706A485C;
        int n2;
        if (n != 0) {
            n2 = ((n * 31 >>> 4) % n ^ n >>> 16);
        } else {
            n2 = 0;
        }
        String name = null;
        switch (n2) {
            case 164469854: {
                name = "eu west";
                break;
            }
            case 164469848: {
                name = "eu central";
                break;
            }
            default: {
                name = mode.name();
                break;
            }
        }
        return name;
    }

    private void handleAutoEat() {
        final Module moduleByClass = Radon.INSTANCE.MODULE_MANAGER.getModuleByClass(AutoEat.class);
        if (!moduleByClass.isEnabled()) {
            this.handleBlockBreaking(true);
            return;
        }
        if (((AutoEat) moduleByClass).shouldEat()) {
            return;
        }
        this.handleBlockBreaking(true);
    }

    private void handleBlockBreaking(final boolean b) {
        if (mc.player.getPitch() != 89.9f) {
            mc.player.setPitch(89.9f);
        }
        if (!mc.player.isUsingItem()) {
            if (b && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                final BlockHitResult blockHitResult = (BlockHitResult) mc.crosshairTarget;
                final BlockPos blockPos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
                if (!mc.world.getBlockState(blockPos).isAir()) {
                    final Direction side = blockHitResult.getSide();
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

    private void scanForEntities() {
        int n = 0;
        int n2 = 0;
        BlockPos blockPos = null;
        final Iterator iterator = BlockUtil.getLoadedChunks().iterator();
        while (iterator.hasNext()) {
            for (final Object next : ((WorldChunk) iterator.next()).getBlockEntityPositions()) {
                final BlockEntity getBlockEntity = mc.world.getBlockEntity((BlockPos) next);
                if (this.spawn.getValue() && getBlockEntity instanceof MobSpawnerBlockEntity) {
                    final String string = ((MobSpawnerLogicAccessor) ((MobSpawnerBlockEntity) getBlockEntity).getLogic()).getSpawnEntry().getNbt().getString("id").toString();
                    if (string != "minecraft:cave_spider" && string != "minecraft:spider") {
                        ++n2;
                        blockPos = (BlockPos) next;
                    }
                }
                if (getBlockEntity instanceof ChestBlockEntity || getBlockEntity instanceof EnderChestBlockEntity || getBlockEntity instanceof ShulkerBoxBlockEntity || getBlockEntity instanceof FurnaceBlockEntity || getBlockEntity instanceof BarrelBlockEntity || getBlockEntity instanceof EnchantingTableBlockEntity) {
                    ++n;
                }
            }
        }
        if (n2 > 0) {
            ++this.spawnerCounter;
        } else {
            this.spawnerCounter = 0;
        }
        if (this.spawnerCounter > 10) {
            this.notifyBaseOrSpawner("YOU FOUND SPAWNER", blockPos.getX(), blockPos.getY(), blockPos.getZ(), false);
            this.spawnerCounter = 0;
        }
        if (n > this.minStorage.getIntValue()) {
            this.notifyBaseOrSpawner("YOU FOUND BASE", (int) mc.player.getPos().x, (int) mc.player.getPos().y, (int) mc.player.getPos().z, true);
        }
    }

    private void notifyBaseOrSpawner(final String s, final int n, final int n2, final int n3, final boolean b) {
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

    private void notifyTotemExplosion(final String s, final int n, final int n2, final int n3) {
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
            } catch (final Throwable ignored) {
            }
        }
        this.disconnectWithMessage(Text.of(s));
    }

    public boolean isRepairingActive() {
        return this.isRepairing;
    }

    enum Mode {
        EUCENTRAL("eucentral", 0),
        EUWEST("euwest", 1),
        EAST("east", 2),
        WEST("west", 3),
        ASIA("asia", 4),
        OCEANIA("oceania", 5),
        RANDOM("random", 6);

        Mode(final String name, final int ordinal) {
        }
    }

}
