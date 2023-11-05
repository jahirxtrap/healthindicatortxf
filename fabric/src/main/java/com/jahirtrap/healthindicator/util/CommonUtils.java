package com.jahirtrap.healthindicator.util;

import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

import java.util.Optional;

public class CommonUtils {
    public static EntityType getEntityType(Entity entity) {
        if (entity instanceof NeutralMob || entity instanceof Llama) return EntityType.NEUTRAL;
        else if (entity instanceof Monster || entity instanceof Slime || entity instanceof Ghast || entity instanceof Hoglin || entity instanceof Phantom || entity instanceof Shulker)
            return EntityType.HOSTILE;
        else return EntityType.PASSIVE;
    }

    public static String getModName(LivingEntity entity) {
        String modId = Registry.ENTITY_TYPE.getKey(entity.getType()).getNamespace();
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(modId);
        String displayName = modContainerOptional.get().getMetadata().getName();
        return displayName;
    }

    public static Integer getColor(int defaultValue, String hexColor) {
        int color = defaultValue;
        try {
            if (hexColor.startsWith("#")) {
                hexColor = hexColor.substring(1);
            }
            color = Integer.parseInt(hexColor, 16);
        } catch (Exception ignore) {
        }
        return color;
    }

    public static String formatText(float amount) {
        return String.format("%.1f", amount);
    }

    public static int getHudWidth() {
        return 128;
    }

    public static int getHudHeight() {
        int value = 30;
        switch (HealthIndicatorModConfig.barStyle) {
            case VANILLA -> value -= 1;
        }
        if (!HealthIndicatorModConfig.showBar) {
            switch (HealthIndicatorModConfig.barStyle) {
                case VANILLA -> value -= 5;
                default -> value -= 6;
            }
            if (HealthIndicatorModConfig.showModName)
                value -= 2;
        }
        if (!HealthIndicatorModConfig.showName && !HealthIndicatorModConfig.showHealth && !HealthIndicatorModConfig.showArmor)
            value -= 12;
        if (!HealthIndicatorModConfig.showModName)
            value -= 12;
        return value;
    }

    //Damage Particle Utils
    public static float getRedFromColor(int color) {
        return ((color >> 16) & 0xff) / 255F;
    }

    public static float getGreenFromColor(int color) {
        return ((color >> 8) & 0xff) / 255F;
    }

    public static float getBlueFromColor(int color) {
        return ((color) & 0xff) / 255F;
    }

    public static float getAlphaFromColor(int color) {
        return ((color >> 24) & 0xff) / 255F;
    }

    public static int getColorFromRGBA(float red, float green, float blue, float alpha) {
        return ((int) (alpha * 255) << 24) |
                ((int) (red * 255) << 16) |
                ((int) (green * 255) << 8) |
                (int) (blue * 255);
    }

    public static String formatDamageText(float amount) {
        if (amount % 1.0 == 0) {
            return String.format("%.0f", amount);
        } else {
            return String.format("%.1f", amount);
        }
    }

    public enum EntityType {PASSIVE, HOSTILE, NEUTRAL}
}
