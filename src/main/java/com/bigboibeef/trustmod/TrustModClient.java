package com.bigboibeef.trustmod;

import com.bigboibeef.trustmod.events.TrustEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class TrustModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TrustEventHandler.run();
    }
}
