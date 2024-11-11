package net.cowdevs.baseconversions;

import net.cowdevs.baseconversions.util.ModRegistries;
import net.cowdevs.baseconversions.util.BaseConversionsConfig;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseConversions implements ModInitializer {
	public static final String NAME = "Base Conversions";
	public static final String MOD_ID = "base-conversions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final BaseConversionsConfig CONFIG = BaseConversionsConfig.createAndLoad();

	@Override
	public void onInitialize() {
		ModRegistries.registerCommands();
	}
}