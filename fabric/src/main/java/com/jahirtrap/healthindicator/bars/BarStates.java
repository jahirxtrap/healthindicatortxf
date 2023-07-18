package com.jahirtrap.healthindicator.bars;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class BarStates {
    private static final Map<Integer, BarState> STATES = new HashMap<>();
    private static int tickCount = 0;

    public static BarState getState(LivingEntity entity) {
        int id = entity.getId();
        BarState state = STATES.get(id);
        if (state == null) {
            state = new BarState(entity);
            STATES.put(id, state);
        }
        return state;
    }

    public static void tick() {
        for (BarState state : STATES.values()) state.tick();

        if (tickCount % 200 == 0) cleanCache();

        tickCount++;
    }

    private static void cleanCache() {
        STATES.entrySet().removeIf(BarStates::stateExpired);
    }

    private static boolean stateExpired(Map.Entry<Integer, BarState> entry) {
        if (entry.getValue() == null) return true;

        MinecraftClient minecraft = MinecraftClient.getInstance();
        Entity entity = null;
        if (minecraft.world != null) entity = minecraft.world.getEntityById(entry.getKey());

        if (!(entity instanceof LivingEntity)) return true;
        if (!minecraft.world.isChunkLoaded(entity.getBlockPos())) return true;

        return !entity.isAlive();
    }
}
