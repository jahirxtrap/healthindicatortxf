package com.jahirtrap.healthindicator;

import com.jahirtrap.healthindicator.init.HealthIndicatorClient;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.util.configlib.TXFConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;

@Mod(HealthIndicatorMod.MODID)
public class HealthIndicatorMod {

    public static final String MODID = "healthindicatortxf";

    public HealthIndicatorMod() {
        TXFConfig.init(MODID, HealthIndicatorModConfig.class);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> TXFConfig.getScreen(parent, MODID)));

        HealthIndicatorClient.init();
    }
}
