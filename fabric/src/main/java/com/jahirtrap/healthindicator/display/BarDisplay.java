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
import net.minecraft.world.entity.player.Player;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class BarDisplay {
    private static final ResourceLocation ARMOR_FULL_SPRITE = new ResourceLocation("hud/armor_full");
    private static final ResourceLocation HEART_CONTAINER_SPRITE = new ResourceLocation("hud/heart/container");
    private static final ResourceLocation HEART_FULL_SPRITE = new ResourceLocation("hud/heart/full");
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

        int armorValue = entity.getArmorValue();
        boolean armor = armorValue > 0;

        String name = getEntityName(entity);
        float health = entity.getHealth() + entity.getAbsorptionAmount();
        float maxHealth = entity.getMaxHealth() + entity.getAbsorptionAmount();
        String healthMax = String.valueOf(Mth.ceil(maxHealth));
        String healthCur = String.valueOf(Math.min(Mth.ceil(health), Integer.parseInt(healthMax)));
        if (HealthIndicatorModConfig.showHealthDecimals) {
            healthMax = formatText(maxHealth);
            healthCur = formatText(Math.min(health, Float.parseFloat(healthMax)));
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

        setInfoWidth(name, armor, healthText, armorText);
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
            renderHeartIcon(guiGraphics, xOffset, entity);
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
        guiGraphics.blitSprite(ARMOR_FULL_SPRITE, x, 1, 9, 9);
    }

    private void renderHeartIcon(GuiGraphics guiGraphics, int x, LivingEntity entity) {
        ResourceLocation icon = HEART_FULL_SPRITE;
        guiGraphics.blitSprite(HEART_CONTAINER_SPRITE, x, 1, 9, 9);

        if (HealthIndicatorModConfig.dynamicHeartTexture) icon = getHeartSprite(entity);
        guiGraphics.blitSprite(icon, x, 1, 9, 9);
    }

    private ResourceLocation getHeartSprite (LivingEntity entity) {
        final ResourceLocation HEART_FROZEN_FULL_SPRITE = new ResourceLocation("hud/heart/frozen_full");
        final ResourceLocation HEART_ABSORBING_FULL_SPRITE = new ResourceLocation("hud/heart/absorbing_full");
        final ResourceLocation HEART_X_FULL_SPRITE = new ResourceLocation("hud/heart/hardcore_full");
        final ResourceLocation HEART_X_FROZEN_FULL_SPRITE = new ResourceLocation("hud/heart/frozen_hardcore_full");
        final ResourceLocation HEART_X_ABSORBING_FULL_SPRITE = new ResourceLocation("hud/heart/absorbing_hardcore_full");

        ResourceLocation icon = HEART_FULL_SPRITE;
        boolean hardcore = false;
        // if (WITHER) icon = WITHERED_SPRITE; if (POISON) icon = POISONED_SPRITE;
        if (entity instanceof Player && mc.level != null && mc.level.getLevelData().isHardcore()) {
            hardcore = true;
            icon = HEART_X_FULL_SPRITE;
        }
        if (entity.getTicksFrozen() >= entity.getTicksRequiredToFreeze()) {
            if (!hardcore) icon = HEART_FROZEN_FULL_SPRITE;
            else icon = HEART_X_FROZEN_FULL_SPRITE;
        }
        else if (entity.getAbsorptionAmount() > 0) {
            if (!hardcore) icon = HEART_ABSORBING_FULL_SPRITE;
            else icon = HEART_X_ABSORBING_FULL_SPRITE;
        }
        return icon;
    }

    public void setInfoWidth(String name, boolean armor, String healthText, String armorText) {
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
