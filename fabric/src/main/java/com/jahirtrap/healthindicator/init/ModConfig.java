package com.jahirtrap.healthindicator.init;

import com.google.common.collect.Lists;
import com.jahirtrap.configlib.TXFConfig;

import java.util.List;

public class ModConfig extends TXFConfig {
    public static final String GENERAL = "general", HUD = "hud", COLORS = "colors", ENTITIES = "entities";

    @Entry(category = GENERAL, name = "Enable Mod")
    public static boolean enableMod = true;
    @Entry(category = GENERAL, name = "Show Hud")
    public static boolean showHud = true;
    @Entry(category = GENERAL, name = "Show Healing Particles")
    public static boolean showHealingParticles = false;
    @Entry(category = GENERAL, name = "Show Damage Particles")
    public static boolean showDamageParticles = true;
    @Entry(category = GENERAL, name = "Show Bar")
    public static boolean showBar = true;
    @Entry(category = GENERAL, name = "Show Secondary Bar")
    public static boolean showSecondaryBar = true;
    @Entry(category = GENERAL, name = "Show Background Bar")
    public static boolean showBackgroundBar = true;
    @Entry(category = GENERAL, name = "Show Name")
    public static boolean showName = true;
    @Entry(category = GENERAL, name = "Show Health")
    public static boolean showHealth = true;
    @Entry(category = GENERAL, name = "Show Armor")
    public static boolean showArmor = true;
    @Entry(category = GENERAL, name = "Show Mod Name")
    public static boolean showModName = false;
    @Entry(category = GENERAL, name = "Show Health Decimals")
    public static boolean showHealthDecimals = false;
    @Entry(category = GENERAL, name = "Health Text Format")
    public static HealthTextFormat healthTextFormat = HealthTextFormat.BOTH;
    @Entry(category = GENERAL, name = "Dynamic Heart Texture")
    public static boolean dynamicHeartTexture = true;
    @Entry(category = GENERAL, name = "Bar Style")
    public static BarStyle barStyle = BarStyle.DEFAULT;

    @Entry(category = HUD, name = "Distance", min = 0, max = Integer.MAX_VALUE)
    public static int distance = 60;
    @Entry(category = HUD, name = "X Value", min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY)
    public static double xValue = 0.0;
    @Entry(category = HUD, name = "Y Value", min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY)
    public static double yValue = 0.0;
    @Entry(category = HUD, name = "Scale", min = 0.0, max = Double.POSITIVE_INFINITY)
    public static double scale = 1.0;
    @Entry(category = HUD, name = "Hide Delay", min = 0, max = Integer.MAX_VALUE)
    public static int hideDelay = 40;
    @Entry(category = HUD, name = "Position")
    public static Position position = Position.TOP_LEFT;

    @Entry(category = COLORS, name = "Hud Background Opacity", min = 0, max = 100, isSlider = true)
    public static int hudBackgroundOpacity = 0;
    @Entry(category = COLORS, name = "Hud Background Color", width = 7, min = 7, isColor = true)
    public static String hudBackgroundColor = "#000000";
    @Entry(category = COLORS, name = "Background Bar Color", width = 7, min = 7, isColor = true)
    public static String backgroundBarColor = "#808080";
    @Entry(category = COLORS, name = "Passive Color", width = 7, min = 7, isColor = true)
    public static String passiveColor = "#00ff00";
    @Entry(category = COLORS, name = "Passive Color Secondary", width = 7, min = 7, isColor = true)
    public static String passiveColorSecondary = "#008000";
    @Entry(category = COLORS, name = "Hostile Color", width = 7, min = 7, isColor = true)
    public static String hostileColor = "#ff0000";
    @Entry(category = COLORS, name = "Hostile Color Secondary", width = 7, min = 7, isColor = true)
    public static String hostileColorSecondary = "#800000";
    @Entry(category = COLORS, name = "Neutral Color", width = 7, min = 7, isColor = true)
    public static String neutralColor = "#0000ff";
    @Entry(category = COLORS, name = "Neutral Color Secondary", width = 7, min = 7, isColor = true)
    public static String neutralColorSecondary = "#000080";
    @Entry(category = COLORS, name = "Mod Name Color", width = 7, min = 7, isColor = true)
    public static String modNameColor = "#5555ff";
    @Entry(category = COLORS, name = "Healing Particle Color", width = 7, min = 7, isColor = true)
    public static String healingParticleColor = "#00fc00";
    @Entry(category = COLORS, name = "Damage Particle Color", width = 7, min = 7, isColor = true)
    public static String damageParticleColor = "#fcfcfc";

    @Entry(category = ENTITIES, name = "Show Invisible Entities")
    public static boolean showInvisibleEntities = true;
    @Entry(category = ENTITIES, name = "Show Bosses")
    public static boolean showBosses = false;
    @Entry(category = ENTITIES, name = "Blacklist")
    public static List<String> blacklist = Lists.newArrayList();
    @Entry(category = ENTITIES, name = "Bar Blacklist")
    public static List<String> barBlacklist = Lists.newArrayList();
    @Entry(category = ENTITIES, name = "Number Particle Blacklist")
    public static List<String> numberParticleBlacklist = Lists.newArrayList();

    public enum Position {TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT}

    public enum HealthTextFormat {CURRENT_HEALTH, MAX_HEALTH, BOTH}

    public enum BarStyle {VANILLA, DEFAULT, ROUNDED, GRADIENT, MINIMALIST, MODERN}
}
