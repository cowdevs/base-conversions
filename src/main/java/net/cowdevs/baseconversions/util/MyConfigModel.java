package net.cowdevs.baseconversions.util;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;

import static net.cowdevs.baseconversions.BaseConversions.MOD_ID;

@Modmenu(modId = MOD_ID)
@Config(name = "base-conversions-config", wrapperName = "BaseConversionsConfig")
public class MyConfigModel {
    @RangeConstraint(min = 1, max = 32)
    public int maxFractionLength = 8;
}
