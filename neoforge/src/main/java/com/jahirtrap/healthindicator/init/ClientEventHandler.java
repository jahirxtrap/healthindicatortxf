package com.jahirtrap.healthindicator.init;

import com.jahirtrap.healthindicator.bars.BarStates;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent.PlayerTickEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandler {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::playerTick);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandler::registerOverlays);
    }

    private static void playerTick(PlayerTickEvent event) {
        if (!event.player.level().isClientSide) return;
        HealthIndicatorClient.HUD.setEntity(
                HealthIndicatorClient.RAYTRACE.getEntityInCrosshair(0, HealthIndicatorModConfig.distance));
        BarStates.tick();
        HealthIndicatorClient.HUD.tick();
    }

    private static void registerOverlays(final RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.POTION_ICONS.id(), "healthindicatortxf_hud", HealthIndicatorClient.HUD::draw);
    }
}
