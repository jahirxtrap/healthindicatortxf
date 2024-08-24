package com.jahirtrap.healthindicator.init;

import com.jahirtrap.healthindicator.bars.BarStates;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent.PlayerTickEvent;

public class ClientEventHandler {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::onPlayerTick);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandler::registerOverlays);
    }

    private static void onPlayerTick(PlayerTickEvent event) {
        if (!event.player.level().isClientSide) return;
        HealthIndicatorClient.HUD.setEntity(HealthIndicatorClient.RAYTRACE.getEntityInCrosshair(0, ModConfig.distance));
        BarStates.tick();
        HealthIndicatorClient.HUD.tick();
    }

    private static void registerOverlays(final RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.POTION_ICONS.id(), new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), "healthindicatortxf_hud"), HealthIndicatorClient.HUD::draw);
    }
}
