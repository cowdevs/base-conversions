package net.cowdevs.logicalconversions;

import net.cowdevs.logicalconversions.util.LogicalConversionsConfig;
import net.cowdevs.logicalconversions.util.ModRegistries;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicalConversions implements ModInitializer {
	public static final String NAME = "Logical Conversions";
	public static final String MOD_ID = "logical-conversions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final LogicalConversionsConfig CONFIG = LogicalConversionsConfig.createAndLoad();

	@Override
	public void onInitialize() {
		ModRegistries.registerCommands();
	}
}