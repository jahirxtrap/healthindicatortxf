package com.jahirtrap.healthindicator.bars;

import com.jahirtrap.healthindicator.HealthIndicatorMod;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.util.CommonUtils;
import com.jahirtrap.healthindicator.util.CommonUtils.EntityType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

import static com.jahirtrap.healthindicator.util.CommonUtils.getColor;

public class HealthBarRenderer {
    private static final Identifier GUI_BARS_TEXTURES = new Identifier(
            HealthIndicatorMod.MODID + ":textures/gui/bars.png");

    public static void render(MatrixStack matrixStack, LivingEntity entity, float width, float height, boolean armor) {
        EntityType entityType = CommonUtils.getEntityType(entity);
        int color = 0x8000ff, color2 = 0x400080;
        if (entityType == EntityType.PASSIVE) {
            color = getColor(0x00ff00, HealthIndicatorModConfig.PASSIVE_COLOR.get());
            color2 = getColor(0x008000, HealthIndicatorModConfig.PASSIVE_COLOR_SECONDARY.get());
        } else if (entityType == EntityType.HOSTILE) {
            color = getColor(0xff0000, HealthIndicatorModConfig.HOSTILE_COLOR.get());
            color2 = getColor(0x800000, HealthIndicatorModConfig.HOSTILE_COLOR_SECONDARY.get());
        } else if (entityType == EntityType.NEUTRAL) {
            color = getColor(0x0000ff, HealthIndicatorModConfig.NEUTRAL_COLOR.get());
            color2 = getColor(0x000080, HealthIndicatorModConfig.NEUTRAL_COLOR_SECONDARY.get());
        }

        BarState state = BarStates.getState(entity);

        float percent = Math.min(1, Math.min(state.health, entity.getMaxHealth()) / entity.getMaxHealth());
        float percent2 = Math.min(state.previousHealthDisplay, entity.getMaxHealth()) / entity.getMaxHealth();
        int zOffset = 0;

        Matrix4f m4f = matrixStack.peek().getPositionMatrix();
        drawBar(m4f, width, height, 1, 0x808080, zOffset++, true, armor);
        drawBar(m4f, width, height, percent2, color2, zOffset++, false, armor);
        drawBar(m4f, width, height, percent, color, zOffset, false, armor);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void drawBar(Matrix4f matrix4f, float width, float height, float percent,
                                int color, int zOffset, boolean back, boolean armor) {
        float c = 0.0078125F; // 1/128
        int u = 0;
        int v = 6 * 6 * 2 + 6;
        if (back) v = 6 * 6 * 2;
        int uw = MathHelper.ceil(128 * percent);
        int vh = 6;
        int y = 12;

        if (!HealthIndicatorModConfig.SHOW_NAME.get() && !HealthIndicatorModConfig.SHOW_HEALTH.get() && (!armor || !HealthIndicatorModConfig.SHOW_ARMOR.get()))
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

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        buffer.vertex(matrix4f, 0, y, zOffset * zOffsetAmount)
                .texture(u * c, v * c).next();
        buffer.vertex(matrix4f, 0, height + y, zOffset * zOffsetAmount)
                .texture(u * c, (v + vh) * c).next();
        buffer.vertex(matrix4f, (float) size, height + y, zOffset * zOffsetAmount)
                .texture((u + uw) * c, (v + vh) * c).next();
        buffer.vertex(matrix4f, (float) size, y, zOffset * zOffsetAmount)
                .texture(((u + uw) * c), v * c).next();
        tessellator.draw();
    }
}
