package com.bigboibeef.trustmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.bigboibeef.trustmod.util.IEntityDataSaver;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class test {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("test")
                .then(CommandManager.argument("username", StringArgumentType.word())
                        .suggests((context, builder) -> CommandSource.suggestMatching(
                                context.getSource().getServer().getPlayerManager().getPlayerList()
                                        .stream().map(player -> player.getGameProfile().getName()), builder))
                        .executes(test::run)));

    }


    private static int run(CommandContext<ServerCommandSource> context) {
        String username = StringArgumentType.getString(context, "username");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sender = source.getPlayer();

        if (sender == null) {
            return 0;
        }

        // Prevent self-trusting
        if (sender.getGameProfile().getName().equalsIgnoreCase(username)) {
            source.sendMessage(Text.literal("You cannot trust yourself.")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)));
            return 0;
        }

        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(username);
        if (targetPlayer == null) {
            source.sendMessage(Text.literal(username + " is not a player.")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)));
            return 0;
        }

        // Ensure sender supports persistent data
        if (sender instanceof IEntityDataSaver) {
            IEntityDataSaver senderDataSaver = (IEntityDataSaver) sender;
            NbtCompound senderData = senderDataSaver.getPersistentData();

            NbtList senderTrustList;
            if (senderData.contains("trustedPlayers", 9)) {
                senderTrustList = senderData.getList("trustedPlayers", 8);
            } else {
                senderTrustList = new NbtList();
            }

            String targetName = targetPlayer.getGameProfile().getName();
            boolean alreadyTrusted = false;
            for (int i = 0; i < senderTrustList.size(); i++) {
                if (senderTrustList.getString(i).equals(targetName)) {
                    alreadyTrusted = true;
                    break;
                }
            }

            if (!alreadyTrusted) {
                senderTrustList.add(NbtString.of(targetName));
                senderData.put("trustedPlayers", senderTrustList);
                source.sendMessage(Text.literal("You have " + targetName + " trusted")
                        .setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
            } else {
                source.sendMessage(Text.literal(targetName + " is in your trusted list.")
                        .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
            }

        } else {
            source.sendMessage(Text.literal("Error: Your player data cannot be saved.")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)));
        }

        return 1;
    }

}