package com.jahirtrap.healthindicator.init;

import com.jahirtrap.healthindicator.display.DamageParticleRenderer.DamageParticle;
import com.jahirtrap.healthindicator.util.CommonUtils;
import com.jahirtrap.healthindicator.util.EntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.WeakHashMap;

import static com.jahirtrap.healthindicator.util.CommonUtils.getColor;

@Mod.EventBusSubscriber
public class DamageParticleEvents {
    public static final WeakHashMap<LivingEntity, EntityData> ENTITY_TRACKER = new WeakHashMap<>();

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingUpdateEvent(final LivingUpdateEvent event) {
        if (!HealthIndicatorModConfig.SHOW_DAMAGE_PARTICLES.get() || !HealthIndicatorModConfig.ENABLE_MOD.get()) return;
        LivingEntity livingEntity = event.getEntityLiving();

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

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoin(final EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        Entity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (entity.equals(player)) ENTITY_TRACKER.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static void onEntityDamaged(LivingEntity livingEntity, EntityData entityData) {
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

        int color = getColor(0xfcfcfc, HealthIndicatorModConfig.DAMAGE_PARTICLE_COLOR.get());

        DamageParticle damageParticle = new DamageParticle(clientLevel, posX, posY, posZ);
        damageParticle.setText(damageString);
        damageParticle.setColor(color);
        damageParticle.setAnimationSize(1, 1.5);
        damageParticle.setAnimationFade(true);
        damageParticle.setLifetime(20);

        Minecraft.getInstance().particleEngine.add(damageParticle);
    }
}
