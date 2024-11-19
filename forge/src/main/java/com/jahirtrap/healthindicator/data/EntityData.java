package com.jahirtrap.healthindicator.data;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityData {
    public float health, lastHealth, damage, lastDamage;
    public long healthStamp, lastHealthStamp, damageStamp, lastDamageStamp, lastUpdate;

    public EntityData(LivingEntity livingEntity) {
        long gameTimeNow = livingEntity.getLevel().getGameTime();

        this.lastUpdate = gameTimeNow;

        if (livingEntity instanceof Player) this.health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
        else this.health = livingEntity.getHealth();
        this.healthStamp = gameTimeNow;
    }

    public void update(LivingEntity livingEntity) {
        long gameTimeNow = livingEntity.getLevel().getGameTime();

        this.lastUpdate = gameTimeNow;

        this.lastHealth = this.health;
        this.lastHealthStamp = this.healthStamp;
        if (livingEntity instanceof Player) this.health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
        else this.health = livingEntity.getHealth();
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
