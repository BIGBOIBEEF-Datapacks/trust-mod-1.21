package com.bigboibeef.trustmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import com.bigboibeef.trustmod.util.IEntityDataSaver;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class TrustAddCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("trust")
                .then(CommandManager.literal("add").then(CommandManager.argument("username", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            return CommandSource.suggestMatching(
                                    context.getSource().getServer().getPlayerManager().getPlayerList()
                                            .stream().map(player -> player.getGameProfile().getName()), builder);
                        }).executes(TrustAddCommand::run))));
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        // Extract the username argument from the command
        String username = StringArgumentType.getString(context, "username");

        // Get the command sender (who ran the command)
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sender = source.getPlayer();

        if (sender == null) {
            //source.sendFeedback(Text.literal("This command must be run by a player."), false);
            return 0;
        }

        // Find the target player by username
        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(username);
        if (targetPlayer == null) {
            //source.sendFeedback(Text.literal("Player '" + username + "' not found."), false);
            return 0;
        }

        // Add the player to the sender's trust list (logic needs to be implemented)
        //source.sendFeedback(Text.literal("You have trusted " + username + "!"), false);

        source.sendMessage(Text.literal("Added "  + username + " to your trusted player list.").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        return 1; // Return 1 if the command executes successfully
    }
}