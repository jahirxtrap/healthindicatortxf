package com.jahirtrap.healthindicator;

import com.jahirtrap.configlib.TXFConfig;
import com.jahirtrap.healthindicator.init.HealthIndicatorClient;
import com.jahirtrap.healthindicator.init.ModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(HealthIndicatorMod.MODID)
public class HealthIndicatorMod {

    public static final String MODID = "healthindicatortxf";

    public HealthIndicatorMod(IEventBus bus) {
        TXFConfig.init(MODID, ModConfig.class);
        HealthIndicatorClient.init(bus);
    }
}
