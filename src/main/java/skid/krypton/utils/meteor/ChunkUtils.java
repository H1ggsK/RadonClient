package skid.krypton.utils.meteor;


import net.minecraft.world.chunk.Chunk;

public class ChunkUtils {

    public static Iterable<Chunk> chunks(boolean onlyWithLoadedNeighbours) {
        return () -> new ChunkIterator(onlyWithLoadedNeighbours);
    }

    public static Iterable<Chunk> chunks() {
        return chunks(false);
    }

}