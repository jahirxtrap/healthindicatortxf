package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.data.BarState;
import com.jahirtrap.healthindicator.data.BarStates;
import com.jahirtrap.healthindicator.init.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;

import static com.jahirtrap.healthindicator.HealthIndicatorMod.MODID;
import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class BarRenderer {
    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation(MODID, "textures/gui/bars.png");

    public static void render(PoseStack poseStack, LivingEntity entity, int width, int height, boolean armor, boolean bar, int wVal1, int wVal2, int oVal1) {
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

        Matrix4f m4f = poseStack.last().pose();

        if (!bar) width = 0;
        if (alpha4 > 0) {
            if (width >= wVal1 && width >= wVal2) oVal1 = 0;
            drawBackground(m4f, color4, alpha4, zOffset++, wVal1, Math.max(Math.max(width, wVal1), wVal2), oVal1);
        }
        if (!bar) return;
        if (ModConfig.showBackgroundBar)
            drawBar(m4f, width, height, 1, color3, zOffset++, true, armor);
        if (ModConfig.showSecondaryBar)
            drawBar(m4f, width, height, percent2, color2, zOffset++, false, armor);
        drawBar(m4f, width, height, percent, color, zOffset, false, armor);
    }

    private static void drawBar(Matrix4f matrix4f, int width, int height, float percent, int color, int zOffset, boolean back, boolean armor) {
        float v = 10;

        switch (ModConfig.barStyle) {
            case VANILLA -> v -= height;
            case DEFAULT -> v += height;
            case ROUNDED -> v += height * 3;
            case GRADIENT -> v += height * 5;
            case MINIMALIST -> v += height * 7;
            case MODERN -> v += height * 9;
        }

        float c = 0.0078125F; // 1/128
        float u = 0;
        if (back) v -= height;
        int uw = Mth.ceil(128 * percent);
        int y = 12;

        if (!ModConfig.showName && !ModConfig.showHealth && (!armor || !ModConfig.showArmor))
            y = 0;

        float size = percent * width;

        float r = (color >> 16 & 255) / 255.0F, g = (color >> 8 & 255) / 255.0F, b = (color & 255) / 255.0F;

        RenderSystem.setShaderColor(r, g, b, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_BARS_TEXTURES);
        RenderSystem.enableBlend();

        float zOffsetAmount = 0.1F;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.vertex(matrix4f, 0, y, zOffset * zOffsetAmount)
                .uv(u * c, v * c).endVertex();
        buffer.vertex(matrix4f, 0, y + height, zOffset * zOffsetAmount)
                .uv(u * c, (v + height) * c).endVertex();
        buffer.vertex(matrix4f, size, y + height, zOffset * zOffsetAmount)
                .uv((u + uw) * c, (v + height) * c).endVertex();
        buffer.vertex(matrix4f, size, y, zOffset * zOffsetAmount)
                .uv((u + uw) * c, v * c).endVertex();
        tesselator.end();
    }

    private static void drawBackground(Matrix4f matrix4f, int color, int alpha, int zOffset, int wVal1, int maxWidth, int minOffset) {
        int padding = 3;
        int xw = maxWidth + minOffset + padding;
        int yh = getHudHeight(wVal1) + 1 + padding;
        int x = minOffset - padding;
        int y = 1 - padding;

        switch (ModConfig.position) {
            case BOTTOM_LEFT, TOP_LEFT -> x -= 1;
            case BOTTOM_RIGHT, TOP_RIGHT -> xw += 1;
        }

        float r = (color >> 16 & 255) / 255.0F, g = (color >> 8 & 255) / 255.0F, b = (color & 255) / 255.0F;

        RenderSystem.setShaderColor(r, g, b, (float) alpha / 100);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.enableBlend();

        float zOffsetAmount = 0.1F;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        buffer.vertex(matrix4f, x, y, zOffset * zOffsetAmount).endVertex();
        buffer.vertex(matrix4f, x, y + yh, zOffset * zOffsetAmount).endVertex();
        buffer.vertex(matrix4f, xw, y + yh, zOffset * zOffsetAmount).endVertex();
        buffer.vertex(matrix4f, xw, y, zOffset * zOffsetAmount).endVertex();
        tesselator.end();
    }
}
