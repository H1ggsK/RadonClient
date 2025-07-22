package skid.krypton.event.events;

import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import skid.krypton.event.CancellableEvent;

import static skid.krypton.Krypton.mc;

public class ChunkDataEvent extends CancellableEvent {
    public ChunkDataS2CPacket packet;
    public WorldChunk chunk;

    public ChunkDataEvent(final ChunkDataS2CPacket packet) {
        this.packet = packet;
        this.chunk = mc.world.getChunk(packet.getChunkX(), packet.getChunkZ());
    }

    public Chunk chunk() {
        return chunk;
    }
}