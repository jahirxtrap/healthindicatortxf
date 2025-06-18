package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.data.BarState;
import com.jahirtrap.healthindicator.data.BarStates;
import com.jahirtrap.healthindicator.init.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.jahirtrap.healthindicator.HealthIndicatorMod.MODID;
import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class BarRenderer {
    private static final ResourceLocation GUI_BARS_TEXTURES = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/bars.png");

    public static void render(GuiGraphics guiGraphics, LivingEntity entity, int width, int height, boolean armor, boolean bar, int wVal1, int wVal2, int oVal1) {
        EntityType entityType = getEntityType(entity);
        int color = 0x8000ff, color2 = 0x400080;
        if (entityType == EntityType.PASSIVE) {
            color = getColor(0x00ff00, ModConfig.passiveColor);
            color2 = getColor(0x008000, ModConfig.passiveColorSecondary);
        } else if (entityType == EntityType.HOSTILE) {
            color = getColor(0xff0000, ModConfig.hostileColor);
            color2 = getColor(0x800000, ModConfig.hostileColorSecondary);
        } else if (entityType == EntityType.NEUTRAL) {
            color = getColor(0x0000ff, ModConfig.neutralColor);
            color2 = getColor(0x000080, ModConfig.neutralColorSecondary);
        }
        int color3 = getColor(0x808080, ModConfig.backgroundBarColor);
        int color4 = getColor(0x000000, ModConfig.hudBackgroundColor);
        int alpha4 = ModConfig.hudBackgroundOpacity;

        BarState state = BarStates.getState(entity);

        float percent = Math.min(1, Math.min(state.health, entity.getMaxHealth()) / entity.getMaxHealth());
        float percent2 = Math.min(state.previousHealthDisplay, entity.getMaxHealth()) / entity.getMaxHealth();

        if (!bar) width = 0;
        if (alpha4 > 0) {
            if (width >= wVal1 && width >= wVal2) oVal1 = 0;
            drawBackground(guiGraphics, color4, alpha4, wVal1, Math.max(Math.max(width, wVal1), wVal2), oVal1);
        }
        if (!bar) return;
        if (ModConfig.showBackgroundBar)
            drawBar(guiGraphics, width, height, 1, color3, true, armor);
        if (ModConfig.showSecondaryBar)
            drawBar(guiGraphics, width, height, percent2, color2, false, armor);
        drawBar(guiGraphics, width, height, percent, color, false, armor);
    }

    private static void drawBar(GuiGraphics guiGraphics, int width, int height, float percent, int color, boolean back, boolean armor) {
        int v = 10;

        switch (ModConfig.barStyle) {
            case VANILLA -> v -= height;
            case DEFAULT -> v += height;
            case ROUNDED -> v += height * 3;
            case GRADIENT -> v += height * 5;
            case MINIMALIST -> v += height * 7;
            case MODERN -> v += height * 9;
        }

        if (back) v -= height;
        int y = (!ModConfig.showName && !ModConfig.showHealth && (!armor || !ModConfig.showArmor)) ? 0 : 12;
        int size = (int) (percent * width);

        int argb = (255 << 24) | (color & 0xffffff);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_BARS_TEXTURES, 0, y, 0, v, size, height, 128, 128, argb);
    }

    private static void drawBackground(GuiGraphics guiGraphics, int color, int alpha, int wVal1, int maxWidth, int minOffset) {
        int padding = 3;
        int xw = maxWidth + minOffset + padding;
        int x = minOffset - padding;

        switch (ModConfig.position) {
            case BOTTOM_LEFT, TOP_LEFT -> x -= 1;
            case BOTTOM_RIGHT, TOP_RIGHT -> xw += 1;
        }

        int yh = getHudHeight(wVal1) + 1 + padding;
        int y = 1 - padding;

        int argb = (Math.round(alpha / 100f * 255) << 24) | (color & 0xffffff);
        guiGraphics.fill(x, y, xw, y + yh, argb);
    }
}
