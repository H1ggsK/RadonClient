package com.h1ggsk.radon.mixin;

import com.h1ggsk.radon.module.modules.client.Radon;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.h1ggsk.radon.commands.Commands;
import com.h1ggsk.radon.event.events.ChunkDataEvent;
import com.h1ggsk.radon.event.events.EntitySpawnEvent;
import com.h1ggsk.radon.event.events.GameJoinedEvent;
import com.h1ggsk.radon.event.events.GameLeftEvent;
import com.h1ggsk.radon.manager.EventManager;

import static com.h1ggsk.radon.Radon.mc;

@Mixin({ClientPlayNetworkHandler.class})
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow
    private ClientWorld world;

    @Unique
    private boolean worldNotNull;

    @Inject(method = {"onChunkData"}, at = {@At("TAIL")})
    private void onChunkData(final ChunkDataS2CPacket packet, final CallbackInfo ci) {
        EventManager.throwEvent(new ChunkDataEvent(packet));
    }

    @Inject(method = {"onEntitySpawn"}, at = {@At("HEAD")}, cancellable = true)
    private void onEntitySpawn(final EntitySpawnS2CPacket packet, final CallbackInfo ci) {
        final EntitySpawnEvent event = new EntitySpawnEvent(packet);
        EventManager.throwEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onGameJoinHead(GameJoinS2CPacket packet, CallbackInfo ci) {
        worldNotNull = world != null;
    }

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
        if (worldNotNull) {
            EventManager.throwEvent(GameLeftEvent.get());
        }
        EventManager.throwEvent(GameJoinedEvent.get());
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        String commandPrefix = Radon.commandPrefix.getValue();
        if (message.startsWith(commandPrefix)) {
            try {
                Commands.dispatch(message.substring(commandPrefix.length()));
            } catch (CommandSyntaxException e) {
                mc.player.sendMessage(Text.of(e.getMessage()), false);
            }

            mc.inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }
}