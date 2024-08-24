package com.jahirtrap.healthindicator.init;

import com.jahirtrap.healthindicator.bars.BarStates;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class ClientEventHandler {
    public static void init(IEventBus bus) {
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::onPlayerTick);
        bus.addListener(ClientEventHandler::registerLayers);
    }

    private static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!event.getEntity().level().isClientSide) return;
        HealthIndicatorClient.HUD.setEntity(HealthIndicatorClient.RAYTRACE.getEntityInCrosshair(0, ModConfig.distance));
        BarStates.tick();
        HealthIndicatorClient.HUD.tick();
    }

    private static void registerLayers(final RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.EFFECTS, new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), "healthindicatortxf_hud"), HealthIndicatorClient.HUD::draw);
    }
}
