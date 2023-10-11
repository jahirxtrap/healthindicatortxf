package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.bars.HealthBarRenderer;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class BarDisplay {
    private static final ResourceLocation ARMOR_FULL_SPRITE = new ResourceLocation("hud/armor_full");
    private static final ResourceLocation HEART_CONTAINER_SPRITE = new ResourceLocation("hud/heart/container");
    private static final ResourceLocation HEART_FULL_SPRITE = new ResourceLocation("hud/heart/full");
    private final Minecraft mc;

    public BarDisplay(Minecraft mc) {
        this.mc = mc;
    }

    private String getEntityName(LivingEntity entity) {
        return entity.getDisplayName().getString();
    }

    public void draw(GuiGraphics guiGraphics, PoseStack poseStack, LivingEntity entity) {
        int barWidth = 128, barHeight = 6;
        int xOffset = 1, xOffsetM = 1;

        switch (HealthIndicatorModConfig.barStyle) {
            case VANILLA -> barHeight = 5;
        }

        int armorValue = entity.getArmorValue();
        boolean armor = armorValue > 0;

        if (HealthIndicatorModConfig.showBar)
            HealthBarRenderer.render(poseStack, entity, barWidth, barHeight, armor);
        else barHeight = 0;

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

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (showName && showHealth && showArmor) guiGraphics.drawString(mc.font, "", xOffset, 2, 0xffffff);

        if (showName && !name.isBlank()) {
            guiGraphics.drawString(mc.font, name, xOffset, 2, 0xffffff);
            xOffset += mc.font.width(name) + 5;
        }
        if (showHealth) {
            renderHeartIcon(guiGraphics, xOffset);
            xOffset += 10;
            guiGraphics.drawString(mc.font, healthText, xOffset, 2, 0xffffff);
            xOffset += mc.font.width(healthText) + 5;
        }
        if (armor && showArmor) {
            renderArmorIcon(guiGraphics, xOffset);
            xOffset += 10;
            guiGraphics.drawString(mc.font, armorText, xOffset, 2, 0xffffff);
        }
        if (showModName && !modNameText.isBlank()) {
            guiGraphics.drawString(mc.font, modNameText, xOffsetM, 15 + ((barHeight == 0) ? barHeight - 2 : barHeight), getColor(0x5555ff, HealthIndicatorModConfig.modNameColor));
        }
    }

    private void renderArmorIcon(GuiGraphics guiGraphics, int x) {
        guiGraphics.blitSprite(ARMOR_FULL_SPRITE, x, 1, 9, 9);
    }

    private void renderHeartIcon(GuiGraphics guiGraphics, int x) {
        guiGraphics.blitSprite(HEART_CONTAINER_SPRITE, x, 1, 9, 9);
        guiGraphics.blitSprite(HEART_FULL_SPRITE, x, 1, 9, 9);
    }
}
