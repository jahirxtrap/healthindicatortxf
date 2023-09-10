package com.jahirtrap.healthindicator;

import com.jahirtrap.healthindicator.display.Hud;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.util.RayTrace;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

public class HealthIndicatorMod implements ModInitializer {

    public static final String MODID = "healthindicatortxf";

    public static Hud HUD = new Hud();
    public static RayTrace RAYTRACE = new RayTrace();

    @Override
    public void onInitialize() {
        MidnightConfig.init(MODID, HealthIndicatorModConfig.class);
    }
}
