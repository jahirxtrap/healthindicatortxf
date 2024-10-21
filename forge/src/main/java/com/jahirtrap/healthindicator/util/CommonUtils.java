package com.jahirtrap.healthindicator.util;

import com.jahirtrap.healthindicator.init.ModConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraftforge.fml.ModList;

import java.util.List;

public class CommonUtils {
    public enum EntityType {PASSIVE, HOSTILE, NEUTRAL}

    public static EntityType getEntityType(Entity entity) {
        if (entity instanceof NeutralMob || entity instanceof Llama) return EntityType.NEUTRAL;
        else if (entity instanceof Monster || entity instanceof Slime || entity instanceof Ghast || entity instanceof Hoglin || entity instanceof Phantom || entity instanceof Shulker)
            return EntityType.HOSTILE;
        else return EntityType.PASSIVE;
    }

    public static String getModName(LivingEntity entity) {
        String modId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace();
        return ModList.get().getModContainerById(modId).get().getModInfo().getDisplayName();
    }

    public static String getEntityId(LivingEntity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }

    public static boolean checkBlacklist(List<String> blacklist, LivingEntity livingEntity) {
        if (!blacklist.isEmpty()) {
            for (String entityId : blacklist) {
                if (getEntityId(livingEntity).equals(entityId)) return true;
            }
        }
        return false;
    }

    public static Integer getColor(int defaultValue, String hexColor) {
        int color = defaultValue;
        try {
            if (hexColor.startsWith("#")) hexColor = hexColor.substring(1);
            color = Integer.parseInt(hexColor, 16);
        } catch (Exception ignore) {
        }
        return color;
    }

    public static String formatText(float amount) {
        amount = (float) (Math.round(amount * 10) / 10.0);
        return String.format("%.1f", amount);
    }

    public static String formatDamageText(float amount) {
        amount = (float) (Math.round(amount * 10) / 10.0);
        if (amount % 1.0 == 0) return String.format("%.0f", amount).replace("-", "+");
        else return String.format("%.1f", amount).replace("-", "+");
    }

    public static int getHudWidth() {
        return 128;
    }

    public static int getHudHeight(int infoWidth) {
        int value = 30;
        if (ModConfig.barStyle == ModConfig.BarStyle.VANILLA) value -= 1;
        if (!ModConfig.showBar) {
            if (ModConfig.barStyle == ModConfig.BarStyle.VANILLA) value -= 5;
            else value -= 6;
            if (ModConfig.showModName) value -= 2;
        }
        if (infoWidth == 0) value -= 12;
        if (!ModConfig.showModName) value -= 12;
        return value;
    }

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
}
