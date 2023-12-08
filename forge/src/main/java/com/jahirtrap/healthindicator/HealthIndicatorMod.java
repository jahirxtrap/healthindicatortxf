package com.jahirtrap.healthindicator;

import com.jahirtrap.healthindicator.init.HealthIndicatorClient;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.util.configlib.TXFConfig;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

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
