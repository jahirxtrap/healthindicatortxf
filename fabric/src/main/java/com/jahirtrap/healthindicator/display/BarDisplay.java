package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.bars.HealthBarRenderer;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.Position;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class BarDisplay {
    private static final ResourceLocation ICON_TEXTURES = new ResourceLocation("textures/gui/icons.png");
    private final Minecraft mc;

    public BarDisplay(Minecraft mc) {
        this.mc = mc;
    }

    private String getEntityName(LivingEntity entity) {
        return entity.getDisplayName().getString();
    }

    public void draw(Position position, PoseStack poseStack, LivingEntity entity) {
        int barWidth = 128;
        int barHeight = 6;
        int xOffset = 1;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        RenderSystem.enableBlend();
        int armorValue = entity.getArmorValue();
        boolean armor = armorValue > 0;

        if (HealthIndicatorModConfig.showBar)
            HealthBarRenderer.render(poseStack, entity, barWidth, barHeight, armor);

        String name = getEntityName(entity);
        int healthMax = Mth.ceil(entity.getMaxHealth());
        int healthCur = Math.min(Mth.ceil(entity.getHealth()), healthMax);
        String healthText = healthCur + "/" + healthMax;
        String armorText = String.valueOf(armorValue);

        switch (HealthIndicatorModConfig.healthTextFormat) {
            case CURRENT_HEALTH -> healthText = String.valueOf(healthCur);
            case MAX_HEALTH -> healthText = String.valueOf(healthMax);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int offAux = 0;
        boolean aux = true;
        boolean showName = HealthIndicatorModConfig.showEntityName;
        boolean showHealth = HealthIndicatorModConfig.showHealth;
        boolean showArmor = HealthIndicatorModConfig.showArmor;
        if (showName) {
            offAux += mc.font.width(name);
            if (showHealth) {
                offAux += 5;
                aux = false;
            }
            if (armor && showArmor) offAux += 5;
        }
        if (showHealth) {
            offAux += mc.font.width(healthText) + 10;
            if (armor && showArmor && aux) offAux += 5;
        }
        if (armor && showArmor) offAux += mc.font.width(armorText) + 10;

        int center = (barWidth / 2) - ((offAux) / 2);
        int right = barWidth - (offAux) - xOffset;

        switch (position) {
            case BOTTOM_CENTER, TOP_CENTER -> xOffset = center;
            case BOTTOM_RIGHT, TOP_RIGHT -> xOffset = right;
        }

        if (showName && showHealth && showArmor) GuiComponent.drawString(poseStack, mc.font, "", xOffset, 2, 0xffffff);

        if (showName) {
            mc.font.drawShadow(poseStack, name, xOffset, 2, 0xffffff);
            xOffset += mc.font.width(name) + 5;
        }
        if (showHealth) {
            renderHeartIcon(poseStack, xOffset);
            xOffset += 10;
            mc.font.drawShadow(poseStack, healthText, xOffset, 2, 0xffffff);
            xOffset += mc.font.width(healthText) + 5;
        }
        if (armor && showArmor) {
            renderArmorIcon(poseStack, xOffset);
            xOffset += 10;
            mc.font.drawShadow(poseStack, armorText, xOffset, 2, 0xffffff);
        }
    }

    private void renderArmorIcon(PoseStack poseStack, int x) {
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        GuiComponent.blit(poseStack, x, 1, 34, 9, 9, 9);
    }

    private void renderHeartIcon(PoseStack poseStack, int x) {
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        GuiComponent.blit(poseStack, x, 1, 16, 0, 9, 9);
        GuiComponent.blit(poseStack, x, 1, 52, 0, 9, 9);
    }
}
