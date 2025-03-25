package com.jahirtrap.healthindicator;

import com.jahirtrap.configlib.TXFConfig;
import com.jahirtrap.healthindicator.init.ModConfig;
import net.neoforged.fml.common.Mod;

@Mod(HealthIndicatorMod.MODID)
public class HealthIndicatorMod {

    public static final String MODID = "healthindicatortxf";

    public HealthIndicatorMod() {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
