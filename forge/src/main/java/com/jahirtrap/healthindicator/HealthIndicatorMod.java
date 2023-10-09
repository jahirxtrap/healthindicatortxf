package com.jahirtrap.healthindicator;

import com.jahirtrap.healthindicator.init.HealthIndicatorClient;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.util.configlib.TXFConfig;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(HealthIndicatorMod.MODID)
public class HealthIndicatorMod {

    public static final String MODID = "healthindicatortxf";

    public HealthIndicatorMod() {
        TXFConfig.init(MODID, HealthIndicatorModConfig.class);
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () ->
                new ConfigGuiHandler.ConfigGuiFactory((client, parent) -> TXFConfig.getScreen(parent, MODID)));

        MinecraftForge.EVENT_BUS.register(this);
        HealthIndicatorClient.init();
    }
}
