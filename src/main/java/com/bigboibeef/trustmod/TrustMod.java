package com.bigboibeef.trustmod;

import com.bigboibeef.trustmod.events.TrustEventHandler;
import com.bigboibeef.trustmod.util.ModCommandRegister;
import com.bigboibeef.trustmod.util.ModEventsRegister;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustMod implements ModInitializer {

	@Override
	public void onInitialize() {
		ModCommandRegister.registerCommands();
		ModEventsRegister.registerEvents();
		TrustEventHandler.run();
	}
}