package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.Position;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class Hud extends Screen {
    private static final TagKey<EntityType<?>> BOSS_TAG = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("forge", "bosses"));
    private final BarDisplay barDisplay;
    private LivingEntity entity;
    private int age;

    public Hud() {
        super(new TextComponent("Health Indicator TXF HUD"));
        this.minecraft = Minecraft.getInstance();
        barDisplay = new BarDisplay(Minecraft.getInstance(), this);
    }

    public void draw(PoseStack poseStack) {
        float scale = HealthIndicatorModConfig.SCALE.get().floatValue();
        if (this.minecraft != null && this.minecraft.options.renderDebug) return;
        float x = determineX();
        float y = determineY();
        draw(poseStack, x, y, scale);
    }

    private float determineX() {
        double scale = HealthIndicatorModConfig.SCALE.get();
        float barWidth = (float) (128 * scale);
        float x = HealthIndicatorModConfig.X_VALUE.get().floatValue();
        Position position = HealthIndicatorModConfig.POSITION.get();
        float wScreen = 0;
        if (minecraft != null) wScreen = minecraft.getWindow().getGuiScaledWidth();

        return switch (position) {
            case BOTTOM_CENTER, TOP_CENTER -> (wScreen / 2) + x - (barWidth / 2);
            case BOTTOM_RIGHT, TOP_RIGHT -> wScreen - x - barWidth;
            default -> x;
        };
    }

    private float determineY() {
        double scale = HealthIndicatorModConfig.SCALE.get();
        int value = 18;
        if (!HealthIndicatorModConfig.SHOW_BAR.get())
            value -= 6;
        if (!HealthIndicatorModConfig.SHOW_NAME.get() && !HealthIndicatorModConfig.SHOW_HEALTH.get() && !HealthIndicatorModConfig.SHOW_ARMOR.get())
            value -= 12;
        float barHeight = (float) (value * scale);
        float y = HealthIndicatorModConfig.Y_VALUE.get().floatValue();
        Position position = HealthIndicatorModConfig.POSITION.get();
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

        if (entity == null && age > HealthIndicatorModConfig.HIDE_DELAY.get()) setEntityWork(null);

        if (entity != null && !HealthIndicatorModConfig.SHOW_BOSSES.get() && entity.getType().is(BOSS_TAG))
            setEntityWork(null);
        else if (entity != null && entity != this.entity) setEntityWork(entity);
    }

    private void draw(PoseStack poseStack, float x, float y, float scale) {
        if (entity == null) return;

        Position position = HealthIndicatorModConfig.POSITION.get();

        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, scale);
        if (HealthIndicatorModConfig.ENABLE_MOD.get()) barDisplay.draw(position, poseStack, entity);
        poseStack.popPose();
    }
}