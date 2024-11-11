package net.cowdevs.baseconversions;

import net.cowdevs.baseconversions.util.ModRegistries;
import net.fabricmc.api.ClientModInitializer;

public class BaseConversionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
		ModRegistries.registerCommands();
    }
}
