package net.cowdevs.logicalconversions.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.cowdevs.logicalconversions.command.BaseCommand;

public class ModRegistries {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> BaseCommand.register(dispatcher));
    }
}
