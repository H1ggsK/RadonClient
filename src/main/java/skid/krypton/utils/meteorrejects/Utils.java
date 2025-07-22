package skid.krypton.utils.meteorrejects;


import net.minecraft.world.chunk.Chunk;

public class Utils {

    public static Iterable<Chunk> chunks(boolean onlyWithLoadedNeighbours) {
        return () -> new ChunkIterator(onlyWithLoadedNeighbours);
    }

    public static Iterable<Chunk> chunks() {
        return chunks(false);
    }

}