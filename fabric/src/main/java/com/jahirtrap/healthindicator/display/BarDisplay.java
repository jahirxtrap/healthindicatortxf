package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.bars.HealthBarRenderer;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class BarDisplay {
    private static final ResourceLocation ICON_TEXTURES = new ResourceLocation("textures/gui/icons.png");
    private final Minecraft mc;
    private int infoWidth;

    public BarDisplay(Minecraft mc) {
        this.mc = mc;
    }

    private String getEntityName(LivingEntity entity) {
        return entity.getDisplayName().getString();
    }

    public void draw(GuiGraphics guiGraphics, PoseStack poseStack, LivingEntity entity) {
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

        boolean showName = HealthIndicatorModConfig.showName;
        boolean showHealth = HealthIndicatorModConfig.showHealth;
        boolean showArmor = HealthIndicatorModConfig.showArmor;
        boolean showModName = HealthIndicatorModConfig.showModName;
        boolean showBar = HealthIndicatorModConfig.showBar;

        setInfoWidth (name, armor, healthText, armorText);
        int offAux = getInfoWidth();

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

        if (showName && showHealth && showArmor) guiGraphics.drawString(mc.font, "", xOffset, 2, 0xffffff);

        if (showName && !name.isBlank()) {
            guiGraphics.drawString(mc.font, name, xOffset, yOffset, 0xffffff);
            xOffset += mc.font.width(name) + 5;
        }
        if (showHealth) {
            renderHeartIcon(guiGraphics, xOffset);
            xOffset += 10;
            guiGraphics.drawString(mc.font, healthText, xOffset, yOffset, 0xffffff);
            xOffset += mc.font.width(healthText) + 5;
        }
        if (armor && showArmor) {
            renderArmorIcon(guiGraphics, xOffset);
            xOffset += 10;
            guiGraphics.drawString(mc.font, armorText, xOffset, yOffset, 0xffffff);
        }
        if (showModName && !modNameText.isBlank()) {
            yOffset = 15 + barHeight;
            if (offAux == 0) yOffset -= 12;
            if (!showBar) yOffset -= barHeight + 2;
            guiGraphics.drawString(mc.font, modNameText, xOffsetM, yOffset, getColor(0x5555ff, HealthIndicatorModConfig.modNameColor));
        }
    }

    private void renderArmorIcon(GuiGraphics guiGraphics, int x) {
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        guiGraphics.blit(ICON_TEXTURES, x, 1, 34, 9, 9, 9);
    }

    private void renderHeartIcon(GuiGraphics guiGraphics, int x) {
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        guiGraphics.blit(ICON_TEXTURES, x, 1, 16, 0, 9, 9);
        guiGraphics.blit(ICON_TEXTURES, x, 1, 52, 0, 9, 9);
    }

    public void setInfoWidth (String name, boolean armor, String healthText, String armorText) {
        int infoWidth = 0;
        boolean aux = true;
        if (HealthIndicatorModConfig.showName && !name.isBlank()) {
            infoWidth += mc.font.width(name);
            if (HealthIndicatorModConfig.showHealth) {
                infoWidth += 5;
                aux = false;
            }
            if (armor && HealthIndicatorModConfig.showArmor) infoWidth += 5;
        }
        if (HealthIndicatorModConfig.showHealth) {
            infoWidth += mc.font.width(healthText) + 10;
            if (armor && HealthIndicatorModConfig.showArmor && aux) infoWidth += 5;
        }
        if (armor && HealthIndicatorModConfig.showArmor) infoWidth += mc.font.width(armorText) + 10;
        this.infoWidth = infoWidth;
    }

    public int getInfoWidth() {
        return this.infoWidth;
    }
}
