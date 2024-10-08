package com.jahirtrap.healthindicator.init;

import com.jahirtrap.healthindicator.bars.BarStates;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class ClientEventHandler {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::onPlayerTick);
    }

    private static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!event.player.level().isClientSide()) return;
        HealthIndicatorClient.HUD.setEntity(HealthIndicatorClient.RAYTRACE.getEntityInCrosshair(0, ModConfig.distance));
        BarStates.tick();
        HealthIndicatorClient.HUD.tick();
    }
}
