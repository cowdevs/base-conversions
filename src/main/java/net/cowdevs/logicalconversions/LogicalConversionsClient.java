package net.cowdevs.logicalconversions;

import net.cowdevs.logicalconversions.util.ModRegistries;
import net.fabricmc.api.ClientModInitializer;

public class LogicalConversionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
		ModRegistries.registerCommands();
    }
}
