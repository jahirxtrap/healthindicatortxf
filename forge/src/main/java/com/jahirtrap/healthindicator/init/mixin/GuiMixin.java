package com.jahirtrap.healthindicator.init.mixin;

import com.jahirtrap.healthindicator.HealthIndicatorMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public abstract class GuiMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void render(PoseStack poseStack, float f, CallbackInfo ci) {
        HealthIndicatorMod.HUD.draw(poseStack);
    }
}
