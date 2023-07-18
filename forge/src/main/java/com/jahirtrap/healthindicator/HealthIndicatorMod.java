package com.jahirtrap.healthindicator;

import com.jahirtrap.healthindicator.init.HealthIndicatorClient;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(HealthIndicatorMod.MODID)
public class HealthIndicatorMod {

    public static final String MODID = "healthindicatortxf";

    public HealthIndicatorMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, HealthIndicatorModConfig.CLIENT_CONFIG);

        MinecraftForge.EVENT_BUS.register(this);
        HealthIndicatorClient.init();
    }
}
