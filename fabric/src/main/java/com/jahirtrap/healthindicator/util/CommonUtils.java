package com.jahirtrap.healthindicator.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.LlamaEntity;

public class CommonUtils {
    public static EntityType getEntityType(Entity entity) {
        if (entity instanceof Angerable || entity instanceof LlamaEntity) return EntityType.NEUTRAL;
        else if (entity instanceof HostileEntity || entity instanceof SlimeEntity || entity instanceof GhastEntity || entity instanceof HoglinEntity || entity instanceof PhantomEntity || entity instanceof ShulkerEntity)
            return EntityType.HOSTILE;
        else return EntityType.PASSIVE;
    }

    public static Integer getColor(int defaultValue, String hexColor) {
        int color = defaultValue;
        try {
            color = Integer.parseInt(hexColor, 16);
        } catch (Exception ignore) {
        }
        return color;
    }

    public enum EntityType {PASSIVE, HOSTILE, NEUTRAL}
}
