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

public class TrustCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("trust")
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("username", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(
                                        context.getSource().getServer().getPlayerManager().getPlayerList()
                                                .stream().map(player -> player.getGameProfile().getName()), builder))
                                .executes(TrustCommand::runAdd)))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("username", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(
                                        context.getSource().getServer().getPlayerManager().getPlayerList()
                                                .stream().map(player -> player.getGameProfile().getName()), builder))
                                .executes(TrustCommand::runRemove)))
                .then(CommandManager.literal("list")
                        .executes(TrustCommand::listTrust)));
    }


    private static int runAdd(CommandContext<ServerCommandSource> context) {
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
                source.sendMessage(Text.literal("You have trusted " + targetName)
                        .setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
            } else {
                source.sendMessage(Text.literal(targetName + " is already in your trusted list.")
                        .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
            }

        } else {
            source.sendMessage(Text.literal("Error: Your player data cannot be saved.")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)));
        }

        return 1;
    }

    private static int runRemove(CommandContext<ServerCommandSource> context) {
        String username = StringArgumentType.getString(context, "username");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sender = source.getPlayer();

        if (sender == null) {
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

            if (senderData.contains("trustedPlayers", 9)) {
                NbtList senderTrustList = senderData.getList("trustedPlayers", 8);

                String targetName = targetPlayer.getGameProfile().getName();
                boolean found = false;

                for (int i = 0; i < senderTrustList.size(); i++) {
                    if (senderTrustList.getString(i).equals(targetName)) {
                        senderTrustList.remove(i);
                        found = true;
                        break;
                    }
                }

                if (found) {
                    senderData.put("trustedPlayers", senderTrustList);
                    source.sendMessage(Text.literal("You have removed " + targetName + " from your trusted list.")
                            .setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
                } else {
                    source.sendMessage(Text.literal(targetName + " is not in your trusted list.")
                            .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
                }
            } else {
                source.sendMessage(Text.literal("You do not have a trust list yet.")
                        .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
            }

        } else {
            source.sendMessage(Text.literal("Error: Your player data cannot be saved.")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)));
        }

        return 1;
    }

    // Command to list trusted players for the sender
    private static int listTrust(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sender = source.getPlayer();
        if (sender == null) {
            return 0;
        }

        if (sender instanceof IEntityDataSaver) {
            IEntityDataSaver dataSaver = (IEntityDataSaver) sender;
            NbtCompound persistentData = dataSaver.getPersistentData();
            NbtList trustList;/////////////////////////////trustList = persistentData.getList("trustedPlayers", 8);
            if (persistentData.contains("trustedPlayers", 9)) {
                trustList = persistentData.getList("trustedPlayers", 8);
            } else {
                trustList = new NbtList();
            }

            if (trustList.isEmpty()) {
                source.sendMessage(Text.literal("You haven't trusted anyone yet.")
                        .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
            } else {
                StringBuilder trustNames = new StringBuilder();
                for (int i = 0; i < trustList.size(); i++) {
                    trustNames.append(trustList.getString(i));
                    if (i < trustList.size() - 1) {
                        trustNames.append(", ");
                    }
                }
                source.sendMessage(Text.literal("Your trusted players: " + trustNames.toString())
                        .setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
            }
        } else {
            source.sendMessage(Text.literal("Error: Your player data cannot be saved.")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)));
        }
        return 1;
    }
}