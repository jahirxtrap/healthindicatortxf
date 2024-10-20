package com.jahirtrap.healthindicator.data;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class BarStates {
    private static final Map<Integer, BarState> STATES = new HashMap<>();
    private static int tickCount = 0;

    public static BarState getState(LivingEntity entity) {
        return STATES.computeIfAbsent(entity.getId(), k -> new BarState(entity));
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

        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = null;
        if (minecraft.level != null) entity = minecraft.level.getEntity(entry.getKey());

        if (!(entity instanceof LivingEntity)) return true;
        if (!minecraft.level.hasChunkAt(entity.blockPosition())) return true;

        return !entity.isAlive();
    }
}
