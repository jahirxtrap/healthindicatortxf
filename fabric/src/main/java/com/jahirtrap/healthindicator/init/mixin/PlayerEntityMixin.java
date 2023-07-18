package com.jahirtrap.healthindicator.init.mixin;

import com.jahirtrap.healthindicator.HealthIndicatorMod;
import com.jahirtrap.healthindicator.bars.BarStates;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        if (!this.getWorld().isClient) {
            return;
        }
        HealthIndicatorMod.HUD.setEntity(HealthIndicatorMod.RAYTRACE.getEntityInCrosshair(0, HealthIndicatorModConfig.DISTANCE.get()));
        BarStates.tick();
        HealthIndicatorMod.HUD.tick();
    }
}
