package skid.krypton.utils.meteor;

import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import skid.krypton.mixin.ClientChunkMapAccessor;
import skid.krypton.utils.ChunkManagerReflect;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static skid.krypton.Krypton.mc;

public class ChunkIterator implements Iterator<Chunk> {
    private final AtomicReferenceArray<WorldChunk> chunks;
    private final int diameter;
    private final int centerChunkX;
    private final int centerChunkZ;
    private final boolean onlyWithLoadedNeighbours;

    private int index = 0;
    private WorldChunk nextChunk;

    public ChunkIterator(boolean onlyWithLoadedNeighbours) {
        ClientChunkManager chunkManager = mc.world.getChunkManager();
        Object rawMap = ChunkManagerReflect.getChunksRaw(chunkManager);
        ClientChunkMapAccessor chunkMap = (ClientChunkMapAccessor) rawMap;

        this.chunks = chunkMap.getChunks();
        this.diameter = chunkMap.getDiameter();
        this.centerChunkX = chunkMap.getCenterChunkX();
        this.centerChunkZ = chunkMap.getCenterChunkZ();
        this.onlyWithLoadedNeighbours = onlyWithLoadedNeighbours;

        findNext();
    }

    private void findNext() {
        nextChunk = null;

        while (index < chunks.length()) {
            WorldChunk chunk = chunks.get(index++);
            if (chunk != null && (!onlyWithLoadedNeighbours || hasAllNeighbors(chunk))) {
                nextChunk = chunk;
                break;
            }
        }
    }

    private boolean hasAllNeighbors(WorldChunk chunk) {
        int x = chunk.getPos().x;
        int z = chunk.getPos().z;

        return isChunkLoaded(x + 1, z) &&
                isChunkLoaded(x - 1, z) &&
                isChunkLoaded(x, z + 1) &&
                isChunkLoaded(x, z - 1);
    }

    private boolean isChunkLoaded(int chunkX, int chunkZ) {
        // Calculate relative position to center
        int relX = Math.floorMod(chunkX - centerChunkX, diameter);
        int relZ = Math.floorMod(chunkZ - centerChunkZ, diameter);

        // Check if within loaded radius
        if (relX >= diameter || relZ >= diameter) {
            return false;
        }

        // Get the chunk from the map
        int index = relZ * diameter + relX;
        return chunks.get(index) != null;
    }

    @Override
    public boolean hasNext() {
        return nextChunk != null;
    }

    @Override
    public WorldChunk next() {
        WorldChunk current = nextChunk;
        findNext();
        return current;
    }
}