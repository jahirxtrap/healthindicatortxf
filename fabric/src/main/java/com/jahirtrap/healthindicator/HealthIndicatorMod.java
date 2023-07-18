package com.jahirtrap.healthindicator;

import com.jahirtrap.healthindicator.display.Hud;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.util.RayTrace;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class HealthIndicatorMod implements ModInitializer {

    public static final String MODID = "healthindicatortxf";

    public static Hud HUD = new Hud();
    public static RayTrace RAYTRACE = new RayTrace();

    @Override
    public void onInitialize() {
        ModLoadingContext.registerConfig(MODID, ModConfig.Type.CLIENT, HealthIndicatorModConfig.CLIENT_CONFIG);
    }
}
