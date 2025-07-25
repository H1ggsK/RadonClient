package skid.krypton.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skid.krypton.event.events.ChunkDataEvent;
import skid.krypton.event.events.EntitySpawnEvent;
import skid.krypton.event.events.GameJoinedEvent;
import skid.krypton.event.events.GameLeftEvent;
import skid.krypton.manager.EventManager;

@Mixin({ClientPlayNetworkHandler.class})
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow
    private ClientWorld world;

    @Unique
    private boolean worldNotNull;

    @Inject(method = {"onChunkData"}, at = {@At("TAIL")})
    private void onChunkData(final ChunkDataS2CPacket packet, final CallbackInfo ci) {
        EventManager.b(new ChunkDataEvent(packet));
    }

    @Inject(method = {"onEntitySpawn"}, at = {@At("HEAD")}, cancellable = true)
    private void onEntitySpawn(final EntitySpawnS2CPacket packet, final CallbackInfo ci) {
        final EntitySpawnEvent event = new EntitySpawnEvent(packet);
        EventManager.b(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onGameJoinHead(GameJoinS2CPacket packet, CallbackInfo ci) {
        worldNotNull = world != null;
    }

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
        if (worldNotNull) {
            EventManager.b(GameLeftEvent.get());
        }
        EventManager.b(GameJoinedEvent.get());
    }
}