package skid.krypton.utils;

import net.minecraft.client.world.ClientChunkManager;
import java.lang.reflect.Field;

public final class ChunkManagerReflect {
    // cached Field instance
    private static final Field CHUNKS_FIELD;

    static {
        try {
            CHUNKS_FIELD = ClientChunkManager.class.getDeclaredField("chunks");
            CHUNKS_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to reflect ClientChunkManager.chunks", e);
        }
    }

    /** 
     * Returns the private ClientChunkMap instance (as raw Object) 
     * so you can cast it at runtime.
     */
    public static Object getChunksRaw(ClientChunkManager manager) {
        try {
            return CHUNKS_FIELD.get(manager);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unexpectedly couldn’t access chunks field", e);
        }
    }
}
