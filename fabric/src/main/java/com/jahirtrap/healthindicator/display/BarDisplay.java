package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.bars.HealthBarRenderer;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.Position;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.Position.*;

public class BarDisplay {
    private static final Identifier ICON_TEXTURES = new Identifier("textures/gui/icons.png");
    private final MinecraftClient mc;

    public BarDisplay(MinecraftClient mc) {
        this.mc = mc;
    }

    private String getEntityName(LivingEntity entity) {
        return entity.getDisplayName().getString();
    }

    public void draw(MatrixStack matrixStack, Position position, LivingEntity entity) {
        int barWidth = 128;
        int barHeight = 6;
        int xOffset = 1;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        RenderSystem.enableBlend();
        int armorValue = entity.getArmor();
        boolean armor = armorValue > 0;

        if (HealthIndicatorModConfig.SHOW_BAR.get())
            HealthBarRenderer.render(matrixStack, entity, barWidth, barHeight, armor);

        String name = getEntityName(entity);
        int healthMax = MathHelper.ceil(entity.getMaxHealth());
        int healthCur = Math.min(MathHelper.ceil(entity.getHealth()), healthMax);
        String healthText = healthCur + "/" + healthMax;
        String armorText = String.valueOf(armorValue);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int offAux = 0;
        boolean aux = true;
        boolean showName = HealthIndicatorModConfig.SHOW_NAME.get();
        boolean showHealth = HealthIndicatorModConfig.SHOW_HEALTH.get();
        boolean showArmor = HealthIndicatorModConfig.SHOW_ARMOR.get();
        if (showName) {
            offAux += mc.textRenderer.getWidth(name);
            if (showHealth) {
                offAux += 5;
                aux = false;
            }
            if (armor && showArmor) offAux += 5;
        }
        if (showHealth) {
            offAux += mc.textRenderer.getWidth(healthText) + 10;
            if (armor && showArmor && aux) offAux += 5;
        }
        if (armor && showArmor) offAux += mc.textRenderer.getWidth(armorText) + 10;

        int center = (barWidth / 2) - ((offAux) / 2);
        int right = barWidth - (offAux) - xOffset;

        if (position == BOTTOM_CENTER || position == TOP_CENTER) xOffset = center;
        else if (position == BOTTOM_RIGHT || position == TOP_RIGHT) xOffset = right;

        if (showName && showHealth && showArmor)
            DrawableHelper.drawTextWithShadow(matrixStack, mc.textRenderer, "", xOffset, 2, 0xffffff);

        if (showName) {
            mc.textRenderer.drawWithShadow(matrixStack, name, xOffset, 2, 0xffffff);
            xOffset += mc.textRenderer.getWidth(name) + 5;
        }
        if (showHealth) {
            renderHeartIcon(matrixStack, xOffset);
            xOffset += 10;
            mc.textRenderer.drawWithShadow(matrixStack, healthText, xOffset, 2, 0xffffff);
            xOffset += mc.textRenderer.getWidth(healthText) + 5;
        }
        if (armor && showArmor) {
            renderArmorIcon(matrixStack, xOffset);
            xOffset += 10;
            mc.textRenderer.drawWithShadow(matrixStack, armorText, xOffset, 2, 0xffffff);
        }
    }

    private void renderArmorIcon(MatrixStack matrixStack, int x) {
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        DrawableHelper.drawTexture(matrixStack, x, 1, 34, 9, 9, 9);
    }

    private void renderHeartIcon(MatrixStack matrixStack, int x) {
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        DrawableHelper.drawTexture(matrixStack, x, 1, 16, 0, 9, 9);
        DrawableHelper.drawTexture(matrixStack, x, 1, 52, 0, 9, 9);
    }
}
