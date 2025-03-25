package com.jahirtrap.healthindicator;

import com.jahirtrap.configlib.TXFConfig;
import com.jahirtrap.healthindicator.init.ModConfig;
import net.fabricmc.api.ModInitializer;

public class HealthIndicatorMod implements ModInitializer {

    public static final String MODID = "healthindicatortxf";

    @Override
    public void onInitialize() {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
