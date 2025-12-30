package com.momosoftworks.scalingmobs.events;

import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import com.momosoftworks.scalingmobs.data.ModRegistries;
import com.momosoftworks.scalingmobs.data.ScalableStat;
import com.momosoftworks.scalingmobs.data.config.MobModifier;
import com.momosoftworks.scalingmobs.data.save_data.LevelScalingData;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class MonsterEvents
{
    public static final UUID DAMAGE_UUID = UUID.randomUUID();
    public static final UUID HEALTH_UUID = UUID.randomUUID();
    public static final UUID SPEED_UUID = UUID.randomUUID();

    @SubscribeEvent
    public static void onMobSpawn(EntityJoinLevelEvent event)
    {
        if (event.getEntity() instanceof LivingEntity living && isScalingMob(living))
        {   initializeAttributeModifiers(living);
        }
    }

    public static void initializeAttributeModifiers(LivingEntity entity)
    {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        double scale = LevelScalingData.get(serverLevel.getServer()).scale();

        AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null)
        {
            damage.removeModifier(MonsterEvents.DAMAGE_UUID);
            damage.addTransientModifier(new AttributeModifier(DAMAGE_UUID, "ScalingMobs:Damage",
                                                              ScalableStat.DAMAGE.getMultiplier(scale),
                                                              AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null)
        {
            float currentHealthPercent = entity.getHealth() / entity.getMaxHealth();

            maxHealth.removeModifier(MonsterEvents.HEALTH_UUID);
            maxHealth.addTransientModifier(new AttributeModifier(HEALTH_UUID, "ScalingMobs:Health",
                                                                 ScalableStat.HEALTH.getMultiplier(scale),
                                                                 AttributeModifier.Operation.MULTIPLY_TOTAL));

            entity.setHealth(entity.getMaxHealth() * currentHealthPercent);
        }

        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null)
        {
            speed.removeModifier(MonsterEvents.SPEED_UUID);
            speed.addTransientModifier(new AttributeModifier(SPEED_UUID, "ScalingMobs:Speed",
                                                             ScalableStat.SPEED.getMultiplier(scale),
                                                             AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    @SubscribeEvent
    public static void onMobDealDamage(LivingDamageEvent event)
    {
        if (event.getEntity() instanceof Player player && player.level() instanceof ServerLevel level
        && event.getSource().getEntity() instanceof LivingEntity living && isScalingMob(living))
        {
            double scale = LevelScalingData.get(level.getServer()).scale();
            float damage = event.getAmount();

            float armorPierceDamage = (float) ScalableStat.ARMOR_PIERCING.getMultiplier(scale) * damage;
            float normalDamage = damage - armorPierceDamage;

            event.setAmount(normalDamage);
            player.setHealth(player.getHealth() - armorPierceDamage);
        }
    }

    @SubscribeEvent
    public static void onMobDrop(LootingLevelEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (isScalingMob(entity) && entity.level() instanceof ServerLevel level)
        {
            double scale = LevelScalingData.get(level.getServer()).scale();

            int oldLooting = event.getLootingLevel();
            int multiplier = Mth.floor(1 + ScalableStat.DROPS.getMultiplier(scale));

            if (oldLooting == 0 && multiplier >= 2)
            {   oldLooting = 1;
            }
            event.setLootingLevel(oldLooting * multiplier);
        }
    }

    @SubscribeEvent
    public static void onMobDropXP(LivingExperienceDropEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (isScalingMob(entity) && entity.level() instanceof ServerLevel level)
        {
            double scale = LevelScalingData.get(level.getServer()).scale();

            int oldXP = event.getOriginalExperience();
            int multiplier = Mth.floor(1 + ScalableStat.EXPERIENCE.getMultiplier(scale));
            if (oldXP == 0 && multiplier >= 2)
            {   oldXP = 1;
            }
            event.setDroppedExperience(oldXP * multiplier);
        }
    }

    @SubscribeEvent
    public static void checkMobSpawnPos(MobSpawnEvent.PositionCheck event)
    {
        LivingEntity entity = event.getEntity();
        for (MobModifier modifier : getModifiersForMob(entity))
        {
            if (!modifier.canEntitySpawn(entity))
            {   event.setResult(Event.Result.DENY);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void applyMobModifiers(MobSpawnEvent.FinalizeSpawn event)
    {
        LivingEntity entity = event.getEntity();
        for (MobModifier modifier : getModifiersForMob(entity))
        {
            for (Map.Entry<Attribute, Double> entry : modifier.attributes().entrySet())
            {
                AttributeInstance attribute = entity.getAttribute(entry.getKey());
                if (attribute != null)
                {   attribute.setBaseValue(entry.getValue());
                }
            }
        }
    }

    private static List<MobModifier> getModifiersForMob(LivingEntity entity)
    {
        if (entity.level() instanceof ServerLevel level)
        {
            Registry<MobModifier> modifierRegistry = level.registryAccess().registryOrThrow(ModRegistries.MOB_MODIFIER);
            return modifierRegistry.stream().filter(modifier -> modifier.hasEntity(entity)).toList();
        }
        return List.of();
    }

    public static boolean isScalingMob(Entity entity)
    {
        String modId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        return (entity instanceof Monster || ScalingMobsConfig.MOB_WHITELIST.get().contains(modId))
            && !ScalingMobsConfig.MOB_BLACKLIST.get().contains(modId);
    }
}
