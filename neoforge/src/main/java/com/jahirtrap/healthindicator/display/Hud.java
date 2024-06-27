package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.Position;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class Hud extends Screen {
    private static final TagKey<EntityType<?>> BOSS_TAG = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", "bosses"));
    private final BarDisplay barDisplay;
    private LivingEntity entity;
    private int age;

    public Hud() {
        super(Component.literal("Health Indicator TXF HUD"));
        this.minecraft = Minecraft.getInstance();
        barDisplay = new BarDisplay(minecraft);
    }

    public void draw(ExtendedGui gui, GuiGraphics guiGraphics, float v, int i, int j) {
        float scale = (float) HealthIndicatorModConfig.scale;
        if (this.minecraft != null && this.minecraft.getDebugOverlay().showDebugScreen()) return;
        float x = determineX();
        float y = determineY();
        draw(guiGraphics, guiGraphics.pose(), x, y, scale);
    }

    private float determineX() {
        float scale = (float) HealthIndicatorModConfig.scale;
        float barWidth = getHudWidth() * scale;
        float x = (float) HealthIndicatorModConfig.xValue;
        Position position = HealthIndicatorModConfig.position;
        float wScreen = 0;
        if (minecraft != null) wScreen = minecraft.getWindow().getGuiScaledWidth();

        float xV = x + 3 * scale;
        return switch (position) {
            case BOTTOM_CENTER, TOP_CENTER -> (wScreen / 2) + x - (barWidth / 2);
            case BOTTOM_RIGHT, TOP_RIGHT -> wScreen - xV - barWidth;
            default -> xV;
        };
    }

    private float determineY() {
        float scale = (float) HealthIndicatorModConfig.scale;
        float barHeight = getHudHeight(barDisplay.getInfoWidth()) * scale;
        float y = (float) HealthIndicatorModConfig.yValue;
        Position position = HealthIndicatorModConfig.position;
        float hScreen = 0;
        if (minecraft != null) hScreen = minecraft.getWindow().getGuiScaledHeight();

        float yV = y + 2 * scale;
        return switch (position) {
            case BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT -> hScreen - yV - barHeight;
            default -> yV;
        };
    }

    public void tick() {
        age++;
    }

    private void setEntityWork(LivingEntity entity) {
        this.entity = entity;
    }

    public void setEntity(LivingEntity entity) {
        if (entity != null) age = 0;

        if (entity == null && age > HealthIndicatorModConfig.hideDelay) setEntityWork(null);

        if (entity != null && !HealthIndicatorModConfig.showInvisibleEntities && entity.isInvisible() && !entity.isCurrentlyGlowing() && !entity.isOnFire())
            setEntityWork(null);
        else if (entity != null && !HealthIndicatorModConfig.showBosses && entity.getType().is(BOSS_TAG))
            setEntityWork(null);
        else if (entity != null && entity != this.entity) setEntityWork(entity);
    }

    private void draw(GuiGraphics guiGraphics, PoseStack poseStack, float x, float y, float scale) {
        if (entity == null) return;
        if (!HealthIndicatorModConfig.showName && !HealthIndicatorModConfig.showHealth && !HealthIndicatorModConfig.showArmor && !HealthIndicatorModConfig.showBar && !HealthIndicatorModConfig.showModName)
            return;
        if (checkBlacklist(HealthIndicatorModConfig.blacklist, entity) || checkBlacklist(HealthIndicatorModConfig.barBlacklist, entity)) return;

        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, scale);
        if (HealthIndicatorModConfig.enableMod && HealthIndicatorModConfig.showHud)
            barDisplay.draw(guiGraphics, poseStack, entity);
        poseStack.popPose();
    }
}
