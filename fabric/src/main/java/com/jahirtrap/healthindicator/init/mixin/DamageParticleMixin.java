package com.jahirtrap.healthindicator.init.mixin;

import com.jahirtrap.healthindicator.display.DamageParticleRenderer.DamageParticle;
import com.jahirtrap.healthindicator.init.HealthIndicatorModConfig;
import com.jahirtrap.healthindicator.util.CommonUtils;
import com.jahirtrap.healthindicator.util.EntityData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.WeakHashMap;

import static com.jahirtrap.healthindicator.util.CommonUtils.checkBlacklist;
import static com.jahirtrap.healthindicator.util.CommonUtils.getColor;

@Mixin(Entity.class)
public abstract class DamageParticleMixin {
    private static final WeakHashMap<LivingEntity, EntityData> ENTITY_TRACKER = new WeakHashMap<>();

    @Environment(EnvType.CLIENT)
    @Inject(method = "tick", at = @At("HEAD"))
    private void onLivingUpdateEvent(CallbackInfo ci) {
        if (!HealthIndicatorModConfig.showDamageParticles || !HealthIndicatorModConfig.enableMod) return;
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (checkBlacklist(HealthIndicatorModConfig.blacklist, livingEntity) || checkBlacklist(HealthIndicatorModConfig.damageParticleBlacklist, livingEntity)) return;

        EntityData entityData = ENTITY_TRACKER.get(livingEntity);

        if (entityData == null) {
            entityData = new EntityData(livingEntity);
            ENTITY_TRACKER.put(livingEntity, entityData);
        } else {
            entityData.update(livingEntity);
        }

        if (entityData.damage != 0) {
            onEntityDamaged(livingEntity, entityData);
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "setLevel", at = @At("HEAD"))
    private void onEntityJoin(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Entity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (entity.equals(player)) ENTITY_TRACKER.clear();
    }

    @Environment(EnvType.CLIENT)
    private static void onEntityDamaged(LivingEntity livingEntity, EntityData entityData) {
        if (entityData.damage < 0) return;

        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        Entity entity = clientLevel.getEntity(livingEntity.getId());
        if (entity == null) return;
        Entity player = Minecraft.getInstance().player;
        if (player == null) return;

        if (livingEntity.equals(player)) return;

        String damageString = CommonUtils.formatDamageText(entityData.damage);

        double posX = livingEntity.getX();
        double posY = (livingEntity.getRemainingFireTicks() > 0) ?
                livingEntity.getBoundingBox().maxY + 1.24 :
                livingEntity.getBoundingBox().maxY + 0.24;
        double posZ = livingEntity.getZ();

        int color = getColor(0xfcfcfc, HealthIndicatorModConfig.damageParticleColor);

        DamageParticle damageParticle = new DamageParticle(clientLevel, posX, posY, posZ);
        damageParticle.setText(damageString);
        damageParticle.setColor(color);
        damageParticle.setAnimationSize(1, 1.5);
        damageParticle.setAnimationFade(true);
        damageParticle.setLifetime(20);

        Minecraft.getInstance().particleEngine.add(damageParticle);
    }
}
