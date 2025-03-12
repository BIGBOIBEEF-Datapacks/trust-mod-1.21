package com.bigboibeef.trustmod.util;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import com.bigboibeef.trustmod.command.TrustAddCommand;

public class ModCommandRegister {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(TrustAddCommand::register);
    }
}