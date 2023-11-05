package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.bars.HealthBarRenderer;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class BarDisplay {
    private static final ResourceLocation ICON_TEXTURES = new ResourceLocation("textures/gui/icons.png");
    private final Minecraft mc;
    private final GuiComponent gui;

    public BarDisplay(Minecraft mc, GuiComponent gui) {
        this.mc = mc;
        this.gui = gui;
    }

    private String getEntityName(LivingEntity entity) {
        return entity.getDisplayName().getString();
    }

    public void draw(PoseStack poseStack, LivingEntity entity) {
        int barWidth = 128, barHeight = 6;
        int xOffset = 1, xOffsetM = 1, yOffset = 2;

        switch (HealthIndicatorModConfig.barStyle) {
            case VANILLA -> barHeight = 5;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        RenderSystem.enableBlend();
        int armorValue = entity.getArmorValue();
        boolean armor = armorValue > 0;

        String name = getEntityName(entity);
        String healthMax = String.valueOf(Mth.ceil(entity.getMaxHealth()));
        String healthCur = String.valueOf(Math.min(Mth.ceil(entity.getHealth()), Integer.parseInt(healthMax)));
        if (HealthIndicatorModConfig.showHealthDecimals) {
            healthMax = formatText(entity.getMaxHealth());
            healthCur = formatText(Math.min(entity.getHealth(), Float.parseFloat(healthMax)));
        }
        String healthText = healthCur + "/" + healthMax;
        String armorText = String.valueOf(armorValue);
        String modNameText = getModName(entity);

        switch (HealthIndicatorModConfig.healthTextFormat) {
            case CURRENT_HEALTH -> healthText = healthCur;
            case MAX_HEALTH -> healthText = healthMax;
        }

        int offAux = 0;
        boolean aux = true;
        boolean showName = HealthIndicatorModConfig.showName;
        boolean showHealth = HealthIndicatorModConfig.showHealth;
        boolean showArmor = HealthIndicatorModConfig.showArmor;
        boolean showModName = HealthIndicatorModConfig.showModName;
        boolean showBar = HealthIndicatorModConfig.showBar;
        if (showName && !name.isBlank()) {
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
        int centerM = (barWidth / 2) - ((mc.font.width(modNameText)) / 2);
        int rightM = barWidth - (mc.font.width(modNameText)) - xOffsetM;

        switch (HealthIndicatorModConfig.position) {
            case BOTTOM_CENTER, TOP_CENTER -> {
                xOffset = center;
                xOffsetM = centerM;
            }
            case BOTTOM_RIGHT, TOP_RIGHT -> {
                xOffset = right;
                xOffsetM = rightM;
            }
        }

        HealthBarRenderer.render(poseStack, entity, barWidth, barHeight, armor, showBar, offAux, mc.font.width(modNameText), Math.min(xOffset, xOffsetM));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (showName && showHealth && showArmor) GuiComponent.drawString(poseStack, mc.font, "", xOffset, 2, 0xffffff);

        if (showName && !name.isBlank()) {
            mc.font.drawShadow(poseStack, name, xOffset, yOffset, 0xffffff);
            xOffset += mc.font.width(name) + 5;
        }
        if (showHealth) {
            renderHeartIcon(poseStack, xOffset);
            xOffset += 10;
            mc.font.drawShadow(poseStack, healthText, xOffset, yOffset, 0xffffff);
            xOffset += mc.font.width(healthText) + 5;
        }
        if (armor && showArmor) {
            renderArmorIcon(poseStack, xOffset);
            xOffset += 10;
            mc.font.drawShadow(poseStack, armorText, xOffset, yOffset, 0xffffff);
        }
        if (showModName && !modNameText.isBlank()) {
            yOffset = 15 + barHeight;
            if (!showName && !showHealth && !showArmor) yOffset -= 15;
            if (!showBar) yOffset -= barHeight + 2;
            mc.font.drawShadow(poseStack, modNameText, xOffsetM, yOffset, getColor(0x5555ff, HealthIndicatorModConfig.modNameColor));
        }
    }

    private void renderArmorIcon(PoseStack poseStack, int x) {
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        gui.blit(poseStack, x, 1, 34, 9, 9, 9);
    }

    private void renderHeartIcon(PoseStack poseStack, int x) {
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        gui.blit(poseStack, x, 1, 16, 0, 9, 9);
        gui.blit(poseStack, x, 1, 52, 0, 9, 9);
    }
}
