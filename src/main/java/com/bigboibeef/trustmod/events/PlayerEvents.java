package com.bigboibeef.trustmod.events;

import com.bigboibeef.trustmod.util.IEntityDataSaver;
import com.bigboibeef.trustmod.util.ModCommandRegister;
import com.bigboibeef.trustmod.util.ModEventsRegister;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEvents implements ServerPlayerEvents.CopyFrom{


    @Override
    public void copyFromPlayer(ServerPlayerEntity serverPlayerEntity, ServerPlayerEntity serverPlayerEntity1, boolean b) {

    }
}
