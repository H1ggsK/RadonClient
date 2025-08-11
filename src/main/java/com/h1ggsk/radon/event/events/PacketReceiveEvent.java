package com.h1ggsk.radon.event.events;

import net.minecraft.network.packet.Packet;
import com.h1ggsk.radon.event.CancellableEvent;

public class PacketReceiveEvent extends CancellableEvent {
    public Packet<?> packet;

    public PacketReceiveEvent(final Packet<?> packet) {
        this.packet = packet;
    }
}