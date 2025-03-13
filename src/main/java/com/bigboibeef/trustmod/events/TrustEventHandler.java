package com.bigboibeef.trustmod.events;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import com.bigboibeef.trustmod.util.IEntityDataSaver;
import net.minecraft.util.Formatting;

public class TrustEventHandler {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof PlayerEntity && entity instanceof PlayerEntity) {
                PlayerEntity attacker = (PlayerEntity) player;
                PlayerEntity target = (PlayerEntity) entity;

                if (attacker instanceof IEntityDataSaver) {
                    IEntityDataSaver attackerDataSaver = (IEntityDataSaver) attacker;
                    NbtCompound attackerData = attackerDataSaver.getPersistentData();

                    NbtList trustedList = attackerData.getList("trustedPlayers", 8);
                    if (trustedList.stream().anyMatch(nbt -> nbt.asString().equalsIgnoreCase(target.getGameProfile().getName()))) {
                        attacker.sendMessage(Text.literal("You cannot attack a trusted player!")
                                .setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
