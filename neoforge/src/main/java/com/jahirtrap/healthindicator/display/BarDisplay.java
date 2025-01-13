package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.init.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class BarDisplay {
    private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_full");
    private static final ResourceLocation HEART_CONTAINER_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/container");
    private static final ResourceLocation HEART_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/full");
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
        if (ModConfig.barStyle == ModConfig.BarStyle.VANILLA) barHeight = 5;

        int armorValue = entity.getArmorValue();
        boolean armor = armorValue > 0;

        String name = getEntityName(entity);
        float health = entity.getHealth() + entity.getAbsorptionAmount();
        float maxHealth = entity.getMaxHealth() + entity.getAbsorptionAmount();
        String healthMax = String.valueOf(Mth.ceil(maxHealth));
        String healthCur = String.valueOf(Math.min(Mth.ceil(health), Integer.parseInt(healthMax)));
        if (ModConfig.showHealthDecimals) {
            healthMax = formatText(maxHealth);
            healthCur = formatText(Math.min(health, maxHealth));
        }
        String healthText = healthCur + "/" + healthMax;
        String armorText = String.valueOf(armorValue);
        String modNameText = getModName(entity);

        switch (ModConfig.healthTextFormat) {
            case CURRENT_HEALTH -> healthText = healthCur;
            case MAX_HEALTH -> healthText = healthMax;
        }

        boolean showName = ModConfig.showName;
        boolean showHealth = ModConfig.showHealth;
        boolean showArmor = ModConfig.showArmor;
        boolean showModName = ModConfig.showModName;
        boolean showBar = ModConfig.showBar;

        setInfoWidth(name, armor, healthText, armorText);
        int offAux = getInfoWidth();

        int center = (barWidth / 2) - ((offAux) / 2);
        int right = barWidth - (offAux) - xOffset;
        int centerM = (barWidth / 2) - ((mc.font.width(modNameText)) / 2);
        int rightM = barWidth - (mc.font.width(modNameText)) - xOffsetM;

        switch (ModConfig.position) {
            case BOTTOM_CENTER, TOP_CENTER -> {
                xOffset = center;
                xOffsetM = centerM;
            }
            case BOTTOM_RIGHT, TOP_RIGHT -> {
                xOffset = right;
                xOffsetM = rightM;
            }
        }

        BarRenderer.render(poseStack, entity, barWidth, barHeight, armor, showBar, offAux, mc.font.width(modNameText), Math.min(xOffset, xOffsetM));
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
            guiGraphics.drawString(mc.font, modNameText, xOffsetM, yOffset, getColor(0x5555ff, ModConfig.modNameColor));
        }
    }

    private void renderArmorIcon(GuiGraphics guiGraphics, int x) {
        guiGraphics.blitSprite(RenderType::guiTextured, ARMOR_FULL_SPRITE, x, 1, 9, 9);
    }

    private void renderHeartIcon(GuiGraphics guiGraphics, int x, LivingEntity entity) {
        ResourceLocation icon = HEART_FULL_SPRITE;
        guiGraphics.blitSprite(RenderType::guiTextured, HEART_CONTAINER_SPRITE, x, 1, 9, 9);

        if (ModConfig.dynamicHeartTexture) icon = getHeartSprite(entity);
        guiGraphics.blitSprite(RenderType::guiTextured, icon, x, 1, 9, 9);
    }

    private ResourceLocation getHeartSprite(LivingEntity entity) {
        final ResourceLocation HEART_FROZEN_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/frozen_full");
        final ResourceLocation HEART_ABSORBING_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full");
        final ResourceLocation HEART_X_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full");
        final ResourceLocation HEART_X_FROZEN_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_full");
        final ResourceLocation HEART_X_ABSORBING_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_full");

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
        } else if (entity.getAbsorptionAmount() > 0) {
            if (!hardcore) icon = HEART_ABSORBING_FULL_SPRITE;
            else icon = HEART_X_ABSORBING_FULL_SPRITE;
        }
        return icon;
    }

    public void setInfoWidth(String name, boolean armor, String healthText, String armorText) {
        int infoWidth = 0;
        boolean aux = true;
        if (ModConfig.showName && !name.isBlank()) {
            infoWidth += mc.font.width(name);
            if (ModConfig.showHealth) {
                infoWidth += 5;
                aux = false;
            }
            if (armor && ModConfig.showArmor) infoWidth += 5;
        }
        if (ModConfig.showHealth) {
            infoWidth += mc.font.width(healthText) + 10;
            if (armor && ModConfig.showArmor && aux) infoWidth += 5;
        }
        if (armor && ModConfig.showArmor) infoWidth += mc.font.width(armorText) + 10;
        this.infoWidth = infoWidth;
    }

    public int getInfoWidth() {
        return this.infoWidth;
    }
}
