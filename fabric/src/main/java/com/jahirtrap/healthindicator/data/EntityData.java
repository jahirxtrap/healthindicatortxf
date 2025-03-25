package com.jahirtrap.healthindicator.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class EntityData {
    public float health, lastHealth, damage, lastDamage;
    public long healthStamp, lastHealthStamp, damageStamp, lastDamageStamp, lastUpdate;

    public EntityData(LivingEntity entity) {
        long gameTimeNow = entity.level().getGameTime();

        this.lastUpdate = gameTimeNow;

        if (entity instanceof Player) this.health = entity.getHealth() + entity.getAbsorptionAmount();
        else this.health = entity.getHealth();
        this.healthStamp = gameTimeNow;
    }

    public void update(LivingEntity entity) {
        long gameTimeNow = entity.level().getGameTime();

        this.lastUpdate = gameTimeNow;

        this.lastHealth = this.health;
        this.lastHealthStamp = this.healthStamp;
        if (entity instanceof Player) this.health = entity.getHealth() + entity.getAbsorptionAmount();
        else this.health = entity.getHealth();
        this.lastHealthStamp = gameTimeNow;

        if (this.health != this.lastHealth) {
            this.lastDamageStamp = this.damageStamp;
            this.lastDamage = this.damage;
            this.damage = this.lastHealth - this.health;
        } else {
            this.damage = 0;
        }
        this.damageStamp = gameTimeNow;
    }
}
