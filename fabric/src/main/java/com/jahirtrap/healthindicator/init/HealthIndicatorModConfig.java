package com.jahirtrap.healthindicator.init;

import eu.midnightdust.lib.config.MidnightConfig;

import static com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.HealthTextFormat.BOTH;
import static com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.Position.TOP_LEFT;

public class HealthIndicatorModConfig extends MidnightConfig {
    @Entry(name = "Enable Mod")
    public static boolean enableMod = true;
    @Entry(name = "Show Hud")
    public static boolean showHud = true;
    @Entry(name = "Show Damage Particles")
    public static boolean showDamageParticles = true;
    @Entry(name = "Show Bar")
    public static boolean showBar = true;
    @Entry(name = "Show Secondary Bar")
    public static boolean showSecondaryBar = true;
    @Entry(name = "Show Background Bar")
    public static boolean showBackgroundBar = true;
    @Entry(name = "Show Name")
    public static boolean showName = true;
    @Entry(name = "Show Health")
    public static boolean showHealth = true;
    @Entry(name = "Show Armor")
    public static boolean showArmor = true;
    @Entry(name = "Show Mod Name")
    public static boolean showModName = false;
    @Entry(name = "Show Bosses")
    public static boolean showBosses = false;
    @Entry(name = "Health Text Format")
    public static HealthTextFormat healthTextFormat = BOTH;
    @Comment
    public static Comment hud;
    @Entry(name = "Distance", min = 0, max = Integer.MAX_VALUE)
    public static int distance = 60;
    @Entry(name = "X Value", min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY)
    public static double xValue = 4.0;
    @Entry(name = "Y Value", min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY)
    public static double yValue = 4.0;
    @Entry(name = "Scale", min = 0.0, max = Double.POSITIVE_INFINITY)
    public static double scale = 1.0;
    @Entry(name = "Hide Delay", min = 0, max = Integer.MAX_VALUE)
    public static int hideDelay = 40;
    @Entry(name = "Position")
    public static Position position = TOP_LEFT;
    @Comment
    public static Comment colors;
    @Entry(name = "Background Color", width = 7, min = 7, isColor = true)
    public static String backgroundColor = "#808080";
    @Entry(name = "Passive Color", width = 7, min = 7, isColor = true)
    public static String passiveColor = "#00ff00";
    @Entry(name = "Passive Color Secondary", width = 7, min = 7, isColor = true)
    public static String passiveColorSecondary = "#008000";
    @Entry(name = "Hostile Color", width = 7, min = 7, isColor = true)
    public static String hostileColor = "#ff0000";
    @Entry(name = "Hostile Color Secondary", width = 7, min = 7, isColor = true)
    public static String hostileColorSecondary = "#800000";
    @Entry(name = "Neutral Color", width = 7, min = 7, isColor = true)
    public static String neutralColor = "#0000ff";
    @Entry(name = "Neutral Color Secondary", width = 7, min = 7, isColor = true)
    public static String neutralColorSecondary = "#000080";
    @Entry(name = "Mod Name Color", width = 7, min = 7, isColor = true)
    public static String modNameColor = "#5555ff";
    @Entry(name = "Damage Particle Color", width = 7, min = 7, isColor = true)
    public static String damageParticleColor = "#fcfcfc";

    public enum Position {TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT}

    public enum HealthTextFormat {CURRENT_HEALTH, MAX_HEALTH, BOTH}
}
