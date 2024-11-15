package net.cowdevs.logicalconversions.util;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;

import static net.cowdevs.logicalconversions.LogicalConversions.MOD_ID;

@Modmenu(modId = MOD_ID)
@Config(name = "logical-conversions-config", wrapperName = "LogicalConversionsConfig")
public class ConfigModel {
    @RangeConstraint(min = 1, max = 32)
    public int maxFractionLength = 8;
}
