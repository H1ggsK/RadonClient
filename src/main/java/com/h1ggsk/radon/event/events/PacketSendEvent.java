package com.h1ggsk.radon.event.events;

import net.minecraft.network.packet.Packet;
import com.h1ggsk.radon.event.CancellableEvent;

public class PacketSendEvent extends CancellableEvent {
    public Packet<?> packet;

    public PacketSendEvent(final Packet<?> packet) {
        this.packet = packet;
    }
    public Packet<?> getPacket() {
        return packet;
    }
    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}