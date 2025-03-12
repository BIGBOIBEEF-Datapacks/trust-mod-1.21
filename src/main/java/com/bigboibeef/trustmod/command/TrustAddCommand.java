package com.bigboibeef.trustmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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
        String username = StringArgumentType.getString(context, "username");

        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sender = source.getPlayer();

        if (sender == null) {
            return 0;
        }

        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(username);
        if (targetPlayer == null) {
            source.sendMessage(Text.literal(username + " is not a player.").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            return 0;
        }

        if (sender instanceof IEntityDataSaver) {
            IEntityDataSaver dataSaver = (IEntityDataSaver) sender;
            NbtCompound persistentData = dataSaver.getPersistentData();
            NbtList trustedList;

            // Check if the list exists (NBT type 9 corresponds to a list)
            if (persistentData.contains("trustedPlayers", 9)) {
                // 8 corresponds to string type in NBT
                trustedList = persistentData.getList("trustedPlayers", 8);
            } else {
                trustedList = new NbtList();
            }

            // Check if the username is already in the trusted list
            boolean alreadyTrusted = false;
            for (int i = 0; i < trustedList.size(); i++) {
                if (trustedList.getString(i).equals(username)) {
                    alreadyTrusted = true;
                    break;
                }
            }

            if (!alreadyTrusted) {
                trustedList.add(NbtString.of(username));
                persistentData.put("trustedPlayers", trustedList);
                source.sendMessage(Text.literal("Added " + username + " to your trusted player list.")
                        .setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
            } else {
                source.sendMessage(Text.literal(username + " is already trusted.")
                        .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
            }
        } else {
            source.sendMessage(Text.literal("Error: Your player data cannot be saved.")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)));
        }

        return 1; // Return 1 if the command executes successfully
    }
}