package com.jahirtrap.healthindicator.init;

import com.jahirtrap.healthindicator.display.Hud;
import com.jahirtrap.healthindicator.util.RayTrace;

public class HealthIndicatorClient {
    public static Hud HUD = new Hud();
    public static RayTrace RAYTRACE = new RayTrace();

    public static void init() {
        ClientEventHandler.init();
    }
}
