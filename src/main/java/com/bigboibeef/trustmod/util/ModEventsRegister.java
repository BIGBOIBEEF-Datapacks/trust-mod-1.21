package com.bigboibeef.trustmod.util;

import com.bigboibeef.trustmod.events.PlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModEventsRegister {
    public static void registerEvents () {
        ServerPlayerEvents.COPY_FROM.register(new PlayerEvents());
    }
}
