package com.jahirtrap.healthindicator.init.mixin;

import com.jahirtrap.healthindicator.display.ParticleRenderer;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.jahirtrap.healthindicator.display.ParticleRenderer.CUSTOM;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {

    @Shadow
    @Final
    @Mutable
    private List<ParticleRenderType> particleRenderOrder;

    @Inject(method = "createParticleGroup", at = @At("HEAD"), cancellable = true)
    private void createParticleGroup(ParticleRenderType type, CallbackInfoReturnable<ParticleGroup<?>> cir) {
        if (type == CUSTOM)
            cir.setReturnValue(new ParticleRenderer.ParticleRendererGroup((ParticleEngine) (Object) this));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        particleRenderOrder = new ArrayList<>(particleRenderOrder);
        particleRenderOrder.add(CUSTOM);
    }
}
