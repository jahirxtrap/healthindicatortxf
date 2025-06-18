package com.jahirtrap.healthindicator.display;

import com.jahirtrap.healthindicator.init.ModConfig;
import com.jahirtrap.healthindicator.init.ModConfig.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.Tags;
import org.joml.Matrix3x2fStack;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class Hud extends Screen {
    private final BarDisplay barDisplay;
    private LivingEntity entity;
    private int age;

    public Hud() {
        super(Component.empty());
        this.minecraft = Minecraft.getInstance();
        barDisplay = new BarDisplay(minecraft);
    }

    public void draw(GuiGraphics guiGraphics) {
        if (this.minecraft != null && (this.minecraft.getDebugOverlay().showDebugScreen() || this.minecraft.options.hideGui))
            return;
        float scale = (float) ModConfig.scale;
        float x = determineX();
        float y = determineY();
        draw(guiGraphics, guiGraphics.pose(), x, y, scale);
    }

    private float determineX() {
        float scale = (float) ModConfig.scale;
        float barWidth = getHudWidth() * scale;
        float x = (float) ModConfig.xValue;
        Position position = ModConfig.position;
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
        float scale = (float) ModConfig.scale;
        float barHeight = getHudHeight(barDisplay.getInfoWidth()) * scale;
        float y = (float) ModConfig.yValue;
        Position position = ModConfig.position;
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

        if (entity == null && age > ModConfig.hideDelay) setEntityWork(null);

        if (entity != null && !ModConfig.showInvisibleEntities && entity.isInvisible() && !entity.isCurrentlyGlowing() && !entity.isOnFire())
            setEntityWork(null);
        else if (entity != null && !ModConfig.showBosses && entity.getType().is(Tags.EntityTypes.BOSSES))
            setEntityWork(null);
        else if (entity != null && entity != this.entity) setEntityWork(entity);
    }

    private void draw(GuiGraphics guiGraphics, Matrix3x2fStack matrix, float x, float y, float scale) {
        if (entity == null) return;
        if (!ModConfig.showName && !ModConfig.showHealth && !ModConfig.showArmor && !ModConfig.showBar && !ModConfig.showModName)
            return;
        if (checkBlacklist(ModConfig.blacklist, entity) || checkBlacklist(ModConfig.barBlacklist, entity)) return;

        matrix.pushMatrix();
        matrix.translate(x, y);
        matrix.scale(scale, scale);
        if (ModConfig.enableMod && ModConfig.showHud)
            barDisplay.draw(guiGraphics, entity);
        matrix.popMatrix();
    }
}
