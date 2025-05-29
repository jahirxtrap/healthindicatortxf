package com.jahirtrap.healthindicator.init.mixin;

import com.jahirtrap.healthindicator.data.EntityData;
import com.jahirtrap.healthindicator.display.ParticleRenderer.DamageParticle;
import com.jahirtrap.healthindicator.init.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.WeakHashMap;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Unique
    private static final WeakHashMap<LivingEntity, EntityData> ENTITY_TRACKER = new WeakHashMap<>();

    @Environment(EnvType.CLIENT)
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if ((!ModConfig.showHealingParticles && !ModConfig.showDamageParticles) || !ModConfig.enableMod) return;
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (checkBlacklist(ModConfig.blacklist, livingEntity) || checkBlacklist(ModConfig.numberParticleBlacklist, livingEntity))
            return;

        EntityData entityData = ENTITY_TRACKER.get(livingEntity);

        if (entityData == null) {
            entityData = new EntityData(livingEntity);
            ENTITY_TRACKER.put(livingEntity, entityData);
        } else {
            entityData.update(livingEntity);
        }

        if (entityData.damage != 0) {
            addParticle(livingEntity, entityData);
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "setLevel", at = @At("HEAD"))
    private void setLevel(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Entity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (entity.equals(player)) ENTITY_TRACKER.clear();
    }

    @Unique
    @Environment(EnvType.CLIENT)
    private static void addParticle(LivingEntity livingEntity, EntityData entityData) {
        if (entityData.damage > 0 && !ModConfig.showDamageParticles) return;
        else if (entityData.damage < 0 && !ModConfig.showHealingParticles) return;
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        Entity entity = clientLevel.getEntity(livingEntity.getId());
        if (entity == null) return;
        Entity player = Minecraft.getInstance().player;
        if (player == null) return;

        if (livingEntity.equals(player)) return;

        String damageString = formatDamageText(entityData.damage);
        int color = getColor(0xfcfcfc, ModConfig.damageParticleColor);
        if (entityData.damage < 0 && !ModConfig.showHealingParticles)
            color = getColor(0x00fc00, ModConfig.healingParticleColor);

        double posX = livingEntity.getX();
        double posY = (livingEntity.getRemainingFireTicks() > 0) ? livingEntity.getBoundingBox().maxY + 1.24 : livingEntity.getBoundingBox().maxY + 0.24;
        double posZ = livingEntity.getZ();

        DamageParticle damageParticle = new DamageParticle(clientLevel, posX, posY, posZ);
        damageParticle.setText(damageString);
        damageParticle.setColor(color);
        damageParticle.setScale(ModConfig.particleScale);
        damageParticle.setAnimationSize(1, 1.5);
        damageParticle.setAnimationFade(true);
        damageParticle.setLifetime(20);

        Minecraft.getInstance().particleEngine.add(damageParticle);
    }
}
