package skid.krypton.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {

    @Accessor("messagePacker")
    MessageChain.Packer getMessagePacker();

    @Accessor("lastSeenMessagesCollector")
    LastSeenMessagesCollector getLastSeenMessagesCollector();
}