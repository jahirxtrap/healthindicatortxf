package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.Position;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class Hud extends Screen {
    private static final TagKey<EntityType<?>> BOSS_TAG = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("c", "bosses"));
    private final BarDisplay barDisplay;
    private LivingEntity entity;
    private int age;

    public Hud() {
        super(Component.literal("Health Indicator TXF HUD"));
        this.minecraft = Minecraft.getInstance();
        barDisplay = new BarDisplay(minecraft, this);
    }

    public void draw(PoseStack poseStack) {
        float scale = (float) HealthIndicatorModConfig.scale;
        if (this.minecraft != null && this.minecraft.options.renderDebug) return;
        float x = determineX();
        float y = determineY();
        draw(poseStack, x, y, scale);
    }

    private float determineX() {
        double scale = HealthIndicatorModConfig.scale;
        float barWidth = (float) (128 * scale);
        float x = (float) HealthIndicatorModConfig.xValue;
        Position position = HealthIndicatorModConfig.position;
        float wScreen = 0;
        if (minecraft != null) wScreen = minecraft.getWindow().getGuiScaledWidth();

        return switch (position) {
            case BOTTOM_CENTER, TOP_CENTER -> (wScreen / 2) + x - (barWidth / 2);
            case BOTTOM_RIGHT, TOP_RIGHT -> wScreen - x - barWidth;
            default -> x;
        };
    }

    private float determineY() {
        double scale = HealthIndicatorModConfig.scale;
        int value = 18;
        if (!HealthIndicatorModConfig.showBar)
            value -= 6;
        if (!HealthIndicatorModConfig.showName && !HealthIndicatorModConfig.showHealth && !HealthIndicatorModConfig.showArmor)
            value -= 12;
        float barHeight = (float) (value * scale);
        float y = (float) HealthIndicatorModConfig.yValue;
        Position position = HealthIndicatorModConfig.position;
        float hScreen = 0;
        if (minecraft != null) hScreen = minecraft.getWindow().getGuiScaledHeight();

        return switch (position) {
            case BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT -> hScreen - y - barHeight;
            default -> y;
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

    private void draw(PoseStack poseStack, float x, float y, float scale) {
        if (entity == null) return;

        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, scale);
        if (HealthIndicatorModConfig.enableMod && HealthIndicatorModConfig.showHud)
            barDisplay.draw(poseStack, entity);
        poseStack.popPose();
    }
}
