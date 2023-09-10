package com.jahirtrap.healthindicator.init;

import net.minecraftforge.common.ForgeConfigSpec;

import static com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.HealthTextFormat.BOTH;
import static com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.Position.TOP_LEFT;

public class HealthIndicatorModConfig {
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD;
    public static final ForgeConfigSpec.BooleanValue SHOW_BAR;
    public static final ForgeConfigSpec.BooleanValue SHOW_BAR_SECONDARY;
    public static final ForgeConfigSpec.BooleanValue SHOW_BAR_BACKGROUND;
    public static final ForgeConfigSpec.BooleanValue SHOW_DAMAGE_PARTICLES;
    public static final ForgeConfigSpec.BooleanValue SHOW_NAME;
    public static final ForgeConfigSpec.BooleanValue SHOW_HEALTH;
    public static final ForgeConfigSpec.BooleanValue SHOW_ARMOR;
    public static final ForgeConfigSpec.BooleanValue SHOW_BOSSES;
    public static final ForgeConfigSpec.IntValue DISTANCE;
    public static final ForgeConfigSpec.DoubleValue X_VALUE;
    public static final ForgeConfigSpec.DoubleValue Y_VALUE;
    public static final ForgeConfigSpec.DoubleValue SCALE;
    public static final ForgeConfigSpec.IntValue HIDE_DELAY;
    public static final ForgeConfigSpec.EnumValue<HealthTextFormat> HEALTH_TEXT_FORMAT;
    public static final ForgeConfigSpec.EnumValue<Position> POSITION;
    public static final ForgeConfigSpec.ConfigValue<String> BACKGROUND_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String> PASSIVE_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String> PASSIVE_COLOR_SECONDARY;
    public static final ForgeConfigSpec.ConfigValue<String> HOSTILE_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String> HOSTILE_COLOR_SECONDARY;
    public static final ForgeConfigSpec.ConfigValue<String> NEUTRAL_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String> NEUTRAL_COLOR_SECONDARY;
    public static final ForgeConfigSpec.ConfigValue<String> DAMAGE_PARTICLE_COLOR;
    public static ForgeConfigSpec CLIENT_CONFIG;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        ENABLE_MOD = CLIENT_BUILDER.comment("Should mod be enabled?").define("enableMod", true);
        SHOW_BAR = CLIENT_BUILDER.comment("Should show bar in HUD?").define("showBar", true);
        SHOW_BAR_SECONDARY = CLIENT_BUILDER.comment("Should show secondary bar?").define("showSecondaryBar", true);
        SHOW_BAR_BACKGROUND = CLIENT_BUILDER.comment("Should show background bar?").define("showBackgroundBar", true);
        SHOW_DAMAGE_PARTICLES = CLIENT_BUILDER.comment("Should show damage particles?").define("showDamageParticles", true);
        SHOW_NAME = CLIENT_BUILDER.comment("Should show entity name?").define("showEntityName", true);
        SHOW_HEALTH = CLIENT_BUILDER.comment("Should show entity health?").define("showHealth", true);
        SHOW_ARMOR = CLIENT_BUILDER.comment("Should show entity armor?").define("showArmor", true);
        SHOW_BOSSES = CLIENT_BUILDER.comment("Should show bosses?").define("showBosses", false);
        HEALTH_TEXT_FORMAT = CLIENT_BUILDER.defineEnum("healthTextFormat", BOTH);
        CLIENT_BUILDER.push("hud");
        DISTANCE = CLIENT_BUILDER.defineInRange("distance", 60, 0, Integer.MAX_VALUE);
        X_VALUE = CLIENT_BUILDER.defineInRange("xValue", 4.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        Y_VALUE = CLIENT_BUILDER.defineInRange("yValue", 4.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        SCALE = CLIENT_BUILDER.defineInRange("scale", 1.0, 0.0, Double.POSITIVE_INFINITY);
        HIDE_DELAY = CLIENT_BUILDER.defineInRange("hideDelay", 40, 0, Integer.MAX_VALUE);
        POSITION = CLIENT_BUILDER.defineEnum("position", TOP_LEFT);
        CLIENT_BUILDER.pop();
        CLIENT_BUILDER.push("colors");
        BACKGROUND_COLOR = CLIENT_BUILDER.comment("Default Value: 808080").define("backgroundColor", "808080");
        PASSIVE_COLOR = CLIENT_BUILDER.comment("Default Value: 00ff00").define("passiveColor", "00ff00");
        PASSIVE_COLOR_SECONDARY = CLIENT_BUILDER.comment("Default Value: 008000").define("passiveColorSecondary", "008000");
        HOSTILE_COLOR = CLIENT_BUILDER.comment("Default Value: ff0000").define("hostileColor", "ff0000");
        HOSTILE_COLOR_SECONDARY = CLIENT_BUILDER.comment("Default Value: 800000").define("hostileColorSecondary", "800000");
        NEUTRAL_COLOR = CLIENT_BUILDER.comment("Default Value: 0000ff").define("neutralColor", "0000ff");
        NEUTRAL_COLOR_SECONDARY = CLIENT_BUILDER.comment("Default Value: 000080").define("neutralColorSecondary", "000080");
        DAMAGE_PARTICLE_COLOR = CLIENT_BUILDER.comment("Default Value: fcfcfc").define("damageParticleColor", "fcfcfc");
        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public enum Position {TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT}

    public enum HealthTextFormat {CURRENT_HEALTH, MAX_HEALTH, BOTH}
}
