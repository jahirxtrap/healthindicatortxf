package com.jahirtrap.healthindicator.init.mixin;

import com.jahirtrap.healthindicator.HealthIndicatorMod;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExtendedGui.class)
public abstract class GuiMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void render(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        HealthIndicatorMod.HUD.draw(guiGraphics);
    }
}
