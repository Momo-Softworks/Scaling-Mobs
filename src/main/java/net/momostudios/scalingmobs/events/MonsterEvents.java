package net.momostudios.scalingmobs.events;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.momostudios.scalingmobs.config.ScalingMobsConfig;

@Mod.EventBusSubscriber
public class MonsterEvents
{
    @SubscribeEvent
    public static void onMobSpawn(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof MonsterEntity)
        {
            int currentDay = (int) (event.getWorld().getDayTime() / 24000L);
            MobEntity mob = (MobEntity) event.getEntity();

            ModifiableAttributeInstance maxHealth = mob.getAttribute(Attributes.MAX_HEALTH);
            ModifiableAttributeInstance damage = mob.getAttribute(Attributes.ATTACK_DAMAGE);

            if (damage != null && maxHealth != null)
            {
                double healthRate = ScalingMobsConfig.getInstance().getMobHealthRate();
                double healthMax = ScalingMobsConfig.getInstance().getMobHealthMax();
                double damageRate = ScalingMobsConfig.getInstance().getMobDamageRate();
                double damageMax = ScalingMobsConfig.getInstance().getMobDamageMax();
                double baseStats = ScalingMobsConfig.getInstance().getMobHealthBase();

                if (ScalingMobsConfig.getInstance().areStatsExponential())
                {
                    maxHealth.setBaseValue(Math.min(healthMax, (maxHealth.getBaseValue() * baseStats) * Math.pow(1 + healthRate, currentDay)));
                    damage.setBaseValue(Math.min(damageMax, (damage.getBaseValue() * baseStats) * Math.pow(1 + damageRate, currentDay)));
                }
                else
                {
                    maxHealth.setBaseValue(Math.min(healthMax, (maxHealth.getBaseValue() * baseStats) * (1 + (currentDay * healthRate))));
                    damage.setBaseValue(Math.min(damageMax, (damage.getBaseValue() * baseStats) * (1 + (currentDay * damageRate))));
                }
                mob.setHealth(mob.getMaxHealth());
            }
        }
    }

    // Piercing Damage
    @SubscribeEvent
    public static void onMobDamage(LivingDamageEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity && event.getSource().getTrueSource() instanceof MonsterEntity)
        {
            int currentDay = (int) (event.getEntity().world.getDayTime() / 24000L);
            double scaleRate = ScalingMobsConfig.getInstance().getPiercingRate();
            double maxPiercing = ScalingMobsConfig.getInstance().getMaxPiercing();
            float damage = event.getAmount();

            float normalDamage = (float) (damage * Math.max(0, 1 - currentDay * Math.min(scaleRate, maxPiercing)));
            float APDamage = (float) Math.min(damage, damage * (currentDay * Math.min(scaleRate, maxPiercing)));

            event.setAmount(normalDamage);
            ((PlayerEntity) event.getEntity()).setHealth(((PlayerEntity) event.getEntity()).getHealth() - APDamage);
        }
    }
}
