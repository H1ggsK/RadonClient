package com.h1ggsk.radon.event.events;

import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import com.h1ggsk.radon.event.CancellableEvent;

public class EntitySpawnEvent extends CancellableEvent {
    public EntitySpawnS2CPacket packet;

    public EntitySpawnEvent(final EntitySpawnS2CPacket packet) {
        this.packet = packet;
    }
}