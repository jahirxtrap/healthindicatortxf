package com.jahirtrap.healthindicator.init.mixin;

import com.jahirtrap.healthindicator.data.EntityData;
import com.jahirtrap.healthindicator.display.ParticleRenderer.DamageParticle;
import com.jahirtrap.healthindicator.init.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.WeakHashMap;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Unique
    private static final WeakHashMap<LivingEntity, EntityData> ENTITY_TRACKER = new WeakHashMap<>();

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!ModConfig.showDamageParticles || !ModConfig.enableMod) return;
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (checkBlacklist(ModConfig.blacklist, livingEntity) || checkBlacklist(ModConfig.damageParticleBlacklist, livingEntity))
            return;

        EntityData entityData = ENTITY_TRACKER.get(livingEntity);

        if (entityData == null) {
            entityData = new EntityData(livingEntity);
            ENTITY_TRACKER.put(livingEntity, entityData);
        } else {
            entityData.update(livingEntity);
        }

        if (entityData.damage != 0) {
            addDamageParticle(livingEntity, entityData);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "getLevel", at = @At("HEAD"))
    private void getLevel(CallbackInfoReturnable<Level> cir) {
        Entity entity = (Entity) (Object) this;
        Entity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (entity.equals(player)) ENTITY_TRACKER.clear();
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private static void addDamageParticle(LivingEntity livingEntity, EntityData entityData) {
        if (entityData.damage < 0) return;

        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        Entity entity = clientLevel.getEntity(livingEntity.getId());
        if (entity == null) return;
        Entity player = Minecraft.getInstance().player;
        if (player == null) return;

        if (livingEntity.equals(player)) return;

        String damageString = formatDamageText(entityData.damage);

        double posX = livingEntity.getX();
        double posY = (livingEntity.getRemainingFireTicks() > 0) ?
                livingEntity.getBoundingBox().maxY + 1.24 :
                livingEntity.getBoundingBox().maxY + 0.24;
        double posZ = livingEntity.getZ();

        int color = getColor(0xfcfcfc, ModConfig.damageParticleColor);

        DamageParticle damageParticle = new DamageParticle(clientLevel, posX, posY, posZ);
        damageParticle.setText(damageString);
        damageParticle.setColor(color);
        damageParticle.setAnimationSize(1, 1.5);
        damageParticle.setAnimationFade(true);
        damageParticle.setLifetime(20);

        Minecraft.getInstance().particleEngine.add(damageParticle);
    }
}