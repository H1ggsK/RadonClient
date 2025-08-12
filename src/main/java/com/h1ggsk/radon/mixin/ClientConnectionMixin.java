package com.h1ggsk.radon.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.h1ggsk.radon.event.events.PacketSendEvent;
import com.h1ggsk.radon.event.events.PacketReceiveEvent;
import com.h1ggsk.radon.manager.EventManager;

@Mixin({ClientConnection.class})
public class ClientConnectionMixin {
    @Inject(method = {"handlePacket"}, at = {@At("HEAD")}, cancellable = true)
    private static void onPacketReceive(final Packet<?> packet, final PacketListener listener, final CallbackInfo ci) {
        final PacketReceiveEvent event = new PacketReceiveEvent(packet);
        EventManager.throwEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = {"send(Lnet/minecraft/network/packet/Packet;)V"}, at = {@At("HEAD")}, cancellable = true)
    private void onPacketSend(final Packet<?> packet, final CallbackInfo ci) {
        final PacketSendEvent event = new PacketSendEvent(packet);
        EventManager.throwEvent(event);
        if (event.isCancelled()) ci.cancel();
    }
}
