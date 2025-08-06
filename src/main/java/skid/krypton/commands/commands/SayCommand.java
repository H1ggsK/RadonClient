package skid.krypton.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import skid.krypton.commands.Command;
import skid.krypton.mixin.ClientPlayNetworkHandlerAccessor;

import java.time.Instant;

public class SayCommand extends Command {
    public SayCommand() {
        super("say", "Sends messages in chat.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            String message = context.getArgument("message", String.class);

            Instant instant = Instant.now();
            long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
            ClientPlayNetworkHandler handler = mc.getNetworkHandler();
            LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = ((ClientPlayNetworkHandlerAccessor) handler).getLastSeenMessagesCollector().collect();
            MessageSignatureData messageSignatureData = ((ClientPlayNetworkHandlerAccessor) handler).getMessagePacker().pack(new MessageBody(message, instant, l, lastSeenMessages.lastSeen()));
            handler.sendPacket(new ChatMessageC2SPacket(message, instant, l, messageSignatureData, lastSeenMessages.update()));

            return SINGLE_SUCCESS;
        }));
    }
}