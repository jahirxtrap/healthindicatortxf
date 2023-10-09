package com.jahirtrap.healthindicator.init;

import com.jahirtrap.healthindicator.bars.BarStates;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class ClientEventHandler {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::playerTick);
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::hudRender);
    }

    private static void playerTick(PlayerTickEvent event) {
        if (!event.player.level.isClientSide) return;
        HealthIndicatorClient.HUD.setEntity(
                HealthIndicatorClient.RAYTRACE.getEntityInCrosshair(0, HealthIndicatorModConfig.distance));
        BarStates.tick();
        HealthIndicatorClient.HUD.tick();
    }

    private static void hudRender(RenderGameOverlayEvent.Post event) {
        if (event.getType().equals(ElementType.ALL)) HealthIndicatorClient.HUD.draw(event.getMatrixStack());
    }
}
