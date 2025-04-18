package com.jahirtrap.healthindicator.init.mixin;

import com.jahirtrap.healthindicator.data.BarStates;
import com.jahirtrap.healthindicator.init.ModConfig;
import com.jahirtrap.healthindicator.util.CommonUtils;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        if (!this.level().isClientSide()) return;
        Player player = (Player) (Object) this;
        if (!ModConfig.showHudWhenBlind && player != null && (player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS)))
            return;
        CommonUtils.HUD.setEntity(CommonUtils.RAYTRACE.getEntityInCrosshair(0, ModConfig.distance));
        BarStates.tick();
        CommonUtils.HUD.tick();
    }
}
