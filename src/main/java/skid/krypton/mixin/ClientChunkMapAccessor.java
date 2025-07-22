package skid.krypton.mixin;

import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.concurrent.atomic.AtomicReferenceArray;

// Target the inner class using its fully qualified name
@Mixin(targets = "net.minecraft.client.world.ClientChunkManager$ClientChunkMap")
public interface ClientChunkMapAccessor {
    @Accessor("chunks")
    AtomicReferenceArray<WorldChunk> getChunks();

    @Accessor("diameter")
    int getDiameter();

    @Accessor("centerChunkX")
    int getCenterChunkX();

    @Accessor("centerChunkZ")
    int getCenterChunkZ();
}