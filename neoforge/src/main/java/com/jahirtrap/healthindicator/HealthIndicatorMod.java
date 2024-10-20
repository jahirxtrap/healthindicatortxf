package com.jahirtrap.healthindicator;

import com.jahirtrap.configlib.TXFConfig;
import com.jahirtrap.healthindicator.display.Hud;
import com.jahirtrap.healthindicator.init.ModConfig;
import com.jahirtrap.healthindicator.util.RayTrace;
import net.neoforged.fml.common.Mod;

@Mod(HealthIndicatorMod.MODID)
public class HealthIndicatorMod {

    public static final String MODID = "healthindicatortxf";

    public static Hud HUD = new Hud();
    public static RayTrace RAYTRACE = new RayTrace();

    public HealthIndicatorMod() {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
