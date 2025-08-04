package skid.krypton.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import skid.krypton.commands.Command;

public class PanicCommand extends Command {
    public PanicCommand() {
        super("panic", "Panic button");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            moduleManager.modules.forEach(module -> {
                if (module.isEnabled()) {
                    module.toggle();
                }
            });
            return SINGLE_SUCCESS;
        });
    }
}