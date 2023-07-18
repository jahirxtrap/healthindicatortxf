package com.jahirtrap.healthindicator.init;

import com.jahirtrap.healthindicator.bars.BarStates;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandler {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::playerTick);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandler::registerOverlays);
    }

    private static void playerTick(PlayerTickEvent event) {
        if (!event.player.level().isClientSide) return;
        HealthIndicatorClient.HUD.setEntity(
                HealthIndicatorClient.RAYTRACE.getEntityInCrosshair(0, HealthIndicatorModConfig.DISTANCE.get()));
        BarStates.tick();
        HealthIndicatorClient.HUD.tick();
    }

    private static void registerOverlays(final RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.POTION_ICONS.id(), "healthindicatortxf_hud", HealthIndicatorClient.HUD::draw);
    }
}
