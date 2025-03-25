package com.jahirtrap.healthindicator.init.mixin;

import com.jahirtrap.healthindicator.util.CommonUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void render(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        CommonUtils.HUD.draw(guiGraphics);
    }
}
