package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.data.BarState;
import com.jahirtrap.healthindicator.data.BarStates;
import com.jahirtrap.healthindicator.init.ModConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;

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
        int zOffset = 0;

        if (!bar) width = 0;
        if (alpha4 > 0) {
            if (width >= wVal1 && width >= wVal2) oVal1 = 0;
            drawBackground(guiGraphics, color4, alpha4, zOffset++, wVal1, Math.max(Math.max(width, wVal1), wVal2), oVal1);
        }
        if (!bar) return;
        if (ModConfig.showBackgroundBar)
            drawBar(guiGraphics, width, height, 1, color3, zOffset++, true, armor);
        if (ModConfig.showSecondaryBar)
            drawBar(guiGraphics, width, height, percent2, color2, zOffset++, false, armor);
        drawBar(guiGraphics, width, height, percent, color, zOffset, false, armor);
    }

    private static void drawBar(GuiGraphics guiGraphics, int width, int height, float percent, int color, int zOffset, boolean back, boolean armor) {
        float v0 = 10;

        switch (ModConfig.barStyle) {
            case VANILLA -> v0 -= height;
            case DEFAULT -> v0 += height;
            case ROUNDED -> v0 += height * 3;
            case GRADIENT -> v0 += height * 5;
            case MINIMALIST -> v0 += height * 7;
            case MODERN -> v0 += height * 9;
        }

        float c = 0.0078125F; // 1/128
        if (back) v0 -= height;
        float u = 0, v = v0;
        int uw = Mth.ceil(128 * percent);
        int x = 0, y;

        y = (!ModConfig.showName && !ModConfig.showHealth && (!armor || !ModConfig.showArmor)) ? 0 : 12;

        float size = percent * width;

        float r = (color >> 16 & 255) / 255.0F, g = (color >> 8 & 255) / 255.0F, b = (color & 255) / 255.0F, a = 1;
        float zOffsetAmount = 0.1F;

        guiGraphics.drawSpecial(buffer -> {
            final VertexConsumer vertex = buffer.getBuffer(RenderType.guiTexturedOverlay(GUI_BARS_TEXTURES));
            Matrix4f matrix = guiGraphics.pose().last().pose();
            float z = zOffset * zOffsetAmount;

            vertex.addVertex(matrix, x, y, z).setUv(u * c, v * c).setColor(r, g, b, a);
            vertex.addVertex(matrix, x, y + height, z).setUv(u * c, (v + height) * c).setColor(r, g, b, a);
            vertex.addVertex(matrix, size, y + height, z).setUv((u + uw) * c, (v + height) * c).setColor(r, g, b, a);
            vertex.addVertex(matrix, size, y, z).setUv((u + uw) * c, v * c).setColor(r, g, b, a);
        });
    }

    private static void drawBackground(GuiGraphics guiGraphics, int color, int alpha, int zOffset, int wVal1, int maxWidth, int minOffset) {
        int padding = 3;
        int xw0 = maxWidth + minOffset + padding;
        int x0 = minOffset - padding;

        switch (ModConfig.position) {
            case BOTTOM_LEFT, TOP_LEFT -> x0 -= 1;
            case BOTTOM_RIGHT, TOP_RIGHT -> xw0 += 1;
        }

        int xw = xw0, yh = getHudHeight(wVal1) + 1 + padding;
        int x = x0, y = 1 - padding;

        float r = (color >> 16 & 255) / 255.0F, g = (color >> 8 & 255) / 255.0F, b = (color & 255) / 255.0F, a = (float) alpha / 100;
        float zOffsetAmount = 0.1F;

        guiGraphics.drawSpecial(buffer -> {
            final VertexConsumer vertex = buffer.getBuffer(RenderType.guiOverlay());
            Matrix4f matrix = guiGraphics.pose().last().pose();
            float z = zOffset * zOffsetAmount;

            vertex.addVertex(matrix, x, y, z).setColor(r, g, b, a);
            vertex.addVertex(matrix, x, y + yh, z).setColor(r, g, b, a);
            vertex.addVertex(matrix, xw, y + yh, z).setColor(r, g, b, a);
            vertex.addVertex(matrix, xw, y, z).setColor(r, g, b, a);
        });
    }
}
