package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig.Position;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Hud extends Screen {
    private static final TagKey<EntityType<?>> BOSS_TAG = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("c", "bosses"));
    private final BarDisplay barDisplay;
    private LivingEntity entity;
    private int age;

    public Hud() {
        super(Text.literal("Health Indicator TXF HUD"));
        this.client = MinecraftClient.getInstance();
        barDisplay = new BarDisplay(MinecraftClient.getInstance(), this);
    }

    public void draw(MatrixStack matrixStack) {
        float scale = HealthIndicatorModConfig.SCALE.get().floatValue();
        if (this.client != null && this.client.options.debugEnabled) return;
        float x = determineX();
        float y = determineY();
        draw(matrixStack, x, y, scale);
    }

    private float determineX() {
        double scale = HealthIndicatorModConfig.SCALE.get();
        float barWidth = (float) (128 * scale);
        float x = HealthIndicatorModConfig.X_VALUE.get().floatValue();
        Position position = HealthIndicatorModConfig.POSITION.get();
        float wScreen = 0;
        if (client != null) wScreen = client.getWindow().getScaledWidth();

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
        if (client != null) hScreen = client.getWindow().getScaledHeight();

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

        if (entity != null && !HealthIndicatorModConfig.SHOW_BOSSES.get() && entity.getType().isIn(BOSS_TAG))
            setEntityWork(null);
        else if (entity != null && entity != this.entity) setEntityWork(entity);
    }

    private void draw(MatrixStack matrixStack, float x, float y, float scale) {
        if (entity == null) return;

        Position position = HealthIndicatorModConfig.POSITION.get();

        matrixStack.push();
        matrixStack.translate(x, y, 0);
        matrixStack.scale(scale, scale, scale);
        if (HealthIndicatorModConfig.ENABLE_MOD.get()) barDisplay.draw(matrixStack, position, entity);
        matrixStack.pop();
    }
}
