package com.bigboibeef.trustmod.events;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import com.bigboibeef.trustmod.util.IEntityDataSaver;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.UUID;


public class TrustEventHandler {
    public static void run() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof PlayerEntity victim) {
                IEntityDataSaver dataSaver = (IEntityDataSaver) player;
                NbtCompound persistentData = dataSaver.getPersistentData();
                NbtList trustList = persistentData.getList("trustedPlayers", 8);

                for (int i = 0; i < trustList.size(); i++) {
                    NbtElement trustedUUID = trustList.get(i);
//                    player.sendMessage(Text.literal("trusted in for (UUID): " + trustedUUID));
//                    player.sendMessage(Text.literal("trusted in for (STRING): " + trustedUUID.toString()));
//                    player.sendMessage(Text.literal("victim in for (STRING): " + victim.toString()));
//                    player.sendMessage(Text.literal("victim in for (NAME): " + victim.getGameProfile().getName()));
//                    player.sendMessage(Text.literal("victim in for (NAME 2): " + "\"" + victim.getGameProfile().getName() + "\""));

                    if (trustedUUID.asString().replace("\"", "").equals(victim.getGameProfile().getName())) {/// THIS IS IT
                        player.sendMessage(Text.literal("You cannot damage a trusted player!")
                                .setStyle(Style.EMPTY.withColor(Formatting.RED)));
                        return ActionResult.FAIL;
                    }

                }
            }
            return ActionResult.PASS;
        });
    }
}
