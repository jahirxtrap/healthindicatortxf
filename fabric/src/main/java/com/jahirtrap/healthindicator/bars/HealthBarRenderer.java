package com.jahirtrap.healthindicator.bars;

import com.jahirtrap.healthindicator.HealthIndicatorMod;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.util.CommonUtils;
import com.jahirtrap.healthindicator.util.CommonUtils.EntityType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import static com.jahirtrap.healthindicator.util.CommonUtils.getColor;

public class HealthBarRenderer {
    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation(
            HealthIndicatorMod.MODID + ":textures/gui/bars.png");

    public static void render(PoseStack poseStack, LivingEntity entity, float width, float height, boolean armor) {
        EntityType entityType = CommonUtils.getEntityType(entity);
        int color = 0x8000ff, color2 = 0x400080, color3 = 0x808080;
        if (entityType == EntityType.PASSIVE) {
            color = getColor(0x00ff00, HealthIndicatorModConfig.passiveColor);
            color2 = getColor(0x008000, HealthIndicatorModConfig.passiveColorSecondary);
        } else if (entityType == EntityType.HOSTILE) {
            color = getColor(0xff0000, HealthIndicatorModConfig.hostileColor);
            color2 = getColor(0x800000, HealthIndicatorModConfig.hostileColorSecondary);
        } else if (entityType == EntityType.NEUTRAL) {
            color = getColor(0x0000ff, HealthIndicatorModConfig.neutralColor);
            color2 = getColor(0x000080, HealthIndicatorModConfig.neutralColorSecondary);
        }
        color3 = getColor(0x808080, HealthIndicatorModConfig.backgroundColor);

        BarState state = BarStates.getState(entity);

        float percent = Math.min(1, Math.min(state.health, entity.getMaxHealth()) / entity.getMaxHealth());
        float percent2 = Math.min(state.previousHealthDisplay, entity.getMaxHealth()) / entity.getMaxHealth();
        int zOffset = 0;

        Matrix4f m4f = poseStack.last().pose();
        if (HealthIndicatorModConfig.showBackgroundBar)
            drawBar(m4f, width, height, 1, color3, zOffset++, true, armor);
        if (HealthIndicatorModConfig.showSecondaryBar)
            drawBar(m4f, width, height, percent2, color2, zOffset++, false, armor);
        drawBar(m4f, width, height, percent, color, zOffset, false, armor);
    }

    private static void drawBar(Matrix4f matrix4f, float width, float height, float percent,
                                int color, int zOffset, boolean back, boolean armor) {
        float c = 0.0078125F; // 1/128
        int u = 0;
        int v = 6 * 6 * 2 + 6;
        if (back) v = 6 * 6 * 2;
        int uw = Mth.ceil(128 * percent);
        int vh = 6;
        int y = 12;

        if (!HealthIndicatorModConfig.showEntityName && !HealthIndicatorModConfig.showHealth && (!armor || !HealthIndicatorModConfig.showArmor))
            y = 0;

        double size = percent * width;

        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

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
        buffer.vertex(matrix4f, 0, height + y, zOffset * zOffsetAmount)
                .uv(u * c, (v + vh) * c).endVertex();
        buffer.vertex(matrix4f, (float) size, height + y, zOffset * zOffsetAmount)
                .uv((u + uw) * c, (v + vh) * c).endVertex();
        buffer.vertex(matrix4f, (float) size, y, zOffset * zOffsetAmount)
                .uv(((u + uw) * c), v * c).endVertex();
        tesselator.end();
    }
}
