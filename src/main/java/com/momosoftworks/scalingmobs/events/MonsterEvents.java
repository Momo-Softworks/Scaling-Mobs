package com.momosoftworks.scalingmobs.events;

import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import com.momosoftworks.scalingmobs.data.save_data.LevelScalingData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class MonsterEvents
{
    @SubscribeEvent
    public static void onMobSpawn(EntityJoinLevelEvent event)
    {
        if (event.getEntity() instanceof LivingEntity living && isScalingMob(living) && living.level() instanceof ServerLevel level)
        {
            double scale = LevelScalingData.get(level).scale();

            AttributeInstance damage = living.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damage != null)
            {
                double damageBase = ScalingMobsConfig.MOB_DAMAGE_BASE.get();
                double damageRate = ScalingMobsConfig.MOB_DAMAGE_RATE.get();
                double damageMax = ScalingMobsConfig.MOB_DAMAGE_MAX.get();

                damage.addTransientModifier(new AttributeModifier("ScalingMobs:Damage",
                                                                  Math.min(damageBase + damageRate * scale, damageMax),
                                                                  AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            AttributeInstance maxHealth = living.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null)
            {
                double healthBase = ScalingMobsConfig.MOB_HEALTH_BASE.get();
                double healthRate = ScalingMobsConfig.MOB_HEALTH_RATE.get();
                double healthMax = ScalingMobsConfig.MOB_HEALTH_MAX.get();
                float currentHealthPercent = living.getHealth() / living.getMaxHealth();

                System.out.println(healthRate * scale);
                maxHealth.addTransientModifier(new AttributeModifier("ScalingMobs:Health",
                                                                     Math.min(healthBase + healthRate * scale, healthMax),
                                                                     AttributeModifier.Operation.MULTIPLY_TOTAL));

                living.setHealth(living.getMaxHealth() * currentHealthPercent);
            }

            AttributeInstance speed = living.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null)
            {
                double speedRate = ScalingMobsConfig.MOB_SPEED_RATE.get();
                double speedMax = ScalingMobsConfig.MOB_SPEED_MAX.get();
                double speedBase = ScalingMobsConfig.MOB_SPEED_BASE.get();

                speed.addTransientModifier(new AttributeModifier("ScalingMobs:Speed",
                                                                 Math.min(speedBase + speedRate * scale, speedMax),
                                                                 AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
    }

    // Piercing Damage
    @SubscribeEvent
    public static void onMobDealDamage(LivingDamageEvent event)
    {
        if (event.getEntity() instanceof Player player && player.level() instanceof ServerLevel level
        && event.getSource().getEntity() instanceof LivingEntity living && isScalingMob(living))
        {
            double scale = LevelScalingData.get(level).scale();
            double pierceRate = ScalingMobsConfig.ARMOR_PIERCING_RATE.get();
            double pierceMax = ScalingMobsConfig.ARMOR_PIERCING_MAX.get();
            double pierceBase = ScalingMobsConfig.ARMOR_PIERCING_BASE.get();
            float damage = event.getAmount();

            float armorPierceDamage = (float) Math.min(pierceMax, pierceBase + (scale * pierceRate)) * damage;
            float normalDamage = damage - armorPierceDamage;

            event.setAmount(normalDamage);
            player.setHealth(player.getHealth() - armorPierceDamage);
        }
    }

    // Multiply mob drops
    @SubscribeEvent
    public static void onMobDrop(LootingLevelEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (isScalingMob(entity) && entity.level() instanceof ServerLevel level)
        {
            double dropRate = ScalingMobsConfig.MOB_DROPS_RATE.get();
            double dropBase = ScalingMobsConfig.MOB_DROPS_BASE.get();
            double maxDrops = ScalingMobsConfig.MOB_DROPS_MAX.get();
            double scale = LevelScalingData.get(level).scale();

            int oldLooting = event.getLootingLevel();
            int multiplier = Mth.floor(1 + Mth.clamp(dropRate * scale, dropBase, maxDrops));

            if (oldLooting == 0 && multiplier >= 2)
            {   oldLooting = 1;
            }
            event.setLootingLevel(oldLooting * multiplier);
        }
    }

    public static boolean isScalingMob(LivingEntity entity)
    {
        String modId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        return (entity instanceof Monster || ScalingMobsConfig.MOB_WHITELIST.get().contains(modId))
            && !ScalingMobsConfig.MOB_BLACKLIST.get().contains(modId);
    }

    public static double getMultipliedStat(double stat, double base, double rate, double max, double scale)
    {
        return stat * (1 + Mth.clamp(rate * scale, base, max));
    }
}
