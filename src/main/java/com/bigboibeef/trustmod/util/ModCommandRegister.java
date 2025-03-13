package com.bigboibeef.trustmod.util;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import com.bigboibeef.trustmod.command.TrustCommand;

public class ModCommandRegister {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(TrustCommand::register);
    }
}