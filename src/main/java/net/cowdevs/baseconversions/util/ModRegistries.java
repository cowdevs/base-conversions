package net.cowdevs.baseconversions.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.cowdevs.baseconversions.command.ConvertCommand;

public class ModRegistries {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ConvertCommand.register(dispatcher));
    }
}
