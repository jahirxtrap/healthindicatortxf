package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.init.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class BarDisplay {
    private static final ResourceLocation ICON_TEXTURES = new ResourceLocation("textures/gui/icons.png");
    private final Minecraft mc;
    private final GuiComponent gui;
    private int infoWidth;

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
        if (ModConfig.barStyle == ModConfig.BarStyle.VANILLA) barHeight = 5;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        RenderSystem.enableBlend();
        int armorValue = entity.getArmorValue();
        boolean armor = armorValue > 0;

        String name = getEntityName(entity);
        float health = entity.getHealth() + entity.getAbsorptionAmount();
        float maxHealth = entity.getMaxHealth() + entity.getAbsorptionAmount();
        String healthMax = String.valueOf(Mth.ceil(maxHealth));
        String healthCur = String.valueOf(Math.min(Mth.ceil(health), Integer.parseInt(healthMax)));
        if (ModConfig.showHealthDecimals) {
            healthMax = formatText(maxHealth);
            healthCur = formatText(Math.min(health, Float.parseFloat(healthMax)));
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

        if (showName && showHealth && showArmor) GuiComponent.drawString(poseStack, mc.font, "", xOffset, 2, 0xffffff);

        if (showName && !name.isBlank()) {
            mc.font.drawShadow(poseStack, name, xOffset, yOffset, 0xffffff);
            xOffset += mc.font.width(name) + 5;
        }
        if (showHealth) {
            renderHeartIcon(poseStack, xOffset, entity);
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
            if (offAux == 0) yOffset -= 12;
            if (!showBar) yOffset -= barHeight + 2;
            mc.font.drawShadow(poseStack, modNameText, xOffsetM, yOffset, getColor(0x5555ff, ModConfig.modNameColor));
        }
    }

    private void renderArmorIcon(PoseStack poseStack, int x) {
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        gui.blit(poseStack, x, 1, 34, 9, 9, 9);
    }

    private void renderHeartIcon(PoseStack poseStack, int x, LivingEntity entity) {
        int u = 52, v = 0;
        RenderSystem.setShaderTexture(0, ICON_TEXTURES);
        gui.blit(poseStack, x, 1, 16, 0, 9, 9);

        if (ModConfig.dynamicHeartTexture) {
            // if (WITHER) u = 124; if (POISON) u = 88;
            if (entity instanceof Player && mc.level != null && mc.level.getLevelData().isHardcore()) v = 45;
            if (entity.getTicksFrozen() >= entity.getTicksRequiredToFreeze()) u = 178;
            else if (entity.getAbsorptionAmount() > 0) u = 160;
        }
        gui.blit(poseStack, x, 1, u, v, 9, 9);
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
