package com.jahirtrap.healthindicator.util;

import net.minecraft.world.entity.LivingEntity;

public class EntityData {
    public float health, lastHealth, damage, lastDamage;
    public long healthStamp, lastHealthStamp, damageStamp, lastDamageStamp, lastUpdate;

    public EntityData(LivingEntity livingEntity) {
        long gameTimeNow = livingEntity.level.getGameTime();

        this.lastUpdate = gameTimeNow;

        this.health = livingEntity.getHealth();
        this.healthStamp = gameTimeNow;
    }

    public void update(LivingEntity livingEntity) {
        long gameTimeNow = livingEntity.level.getGameTime();

        this.lastUpdate = gameTimeNow;

        this.lastHealth = this.health;
        this.lastHealthStamp = this.healthStamp;
        this.health = livingEntity.getHealth();
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
