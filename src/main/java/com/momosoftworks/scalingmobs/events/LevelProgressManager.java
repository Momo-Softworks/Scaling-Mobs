package com.momosoftworks.scalingmobs.events;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.momosoftworks.scalingmobs.api.event.InventoryChangedEvent;
import com.momosoftworks.scalingmobs.api.event.LivingFindTargetEvent;
import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import com.momosoftworks.scalingmobs.data.ModRegistries;
import com.momosoftworks.scalingmobs.data.config.MilestoneData;
import com.momosoftworks.scalingmobs.data.save_data.LevelScalingData;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Function;

@Mod.EventBusSubscriber
public class LevelProgressManager
{
    private static final Multimap<Class<?>, Object> TESTED_OBJECTS = HashMultimap.create();

    @SubscribeEvent
    public static void increaseLevelScaling(TickEvent.LevelTickEvent event)
    {
        if (event.level instanceof ServerLevel level && event.phase == TickEvent.Phase.START && level.getGameTime() % 20 == 0)
        {
            LevelScalingData scalingData = LevelScalingData.get(level);
            boolean exponential = ScalingMobsConfig.EXPONENTIAL_SCALING.get();
            double rate = (ScalingMobsConfig.SCALING_SPEED.get() * 20) / 24000d;
            double newScale = exponential ? Math.pow(Math.pow(scalingData.scale(), 1/2d) + rate, 2d)
                                          : scalingData.scale() + rate;
            scalingData.setScale(newScale);
        }
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event)
    {
        if (event.getEntity().level() instanceof ServerLevel level)
        {
            applyMatchingMilestone(level, MilestoneData::advancements, event.getAdvancement().getId());
        }
    }

    @SubscribeEvent
    public static void onEnterDimension(EntityTravelToDimensionEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            applyMatchingMilestone(player.serverLevel(), MilestoneData::dimensions, event.getDimension());
        }
    }

    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event)
    {
        LivingEntity target = event.getEntity();
        if (target.getLastHurtByMob() instanceof ServerPlayer player)
        {
            applyMatchingMilestone(player.serverLevel(), MilestoneData::killedMobs, target.getType());
        }
    }

    @SubscribeEvent
    public static void onHurtByMob(LivingHurtEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player
        && event.getSource().getEntity() instanceof LivingEntity entity)
        {
            applyMatchingMilestone(player.serverLevel(), MilestoneData::encounteredMobs, entity.getType());
        }
    }

    @SubscribeEvent
    public static void onAttackMob(LivingHurtEvent event)
    {
        if (event.getSource().getEntity() instanceof ServerPlayer player)
        {
            applyMatchingMilestone(player.serverLevel(), MilestoneData::encounteredMobs, event.getEntity().getType());
        }
    }

    @SubscribeEvent
    public static void onTargetedByMob(LivingFindTargetEvent event)
    {
        if (event.getTarget() instanceof ServerPlayer player)
        {
            applyMatchingMilestone(player.serverLevel(), MilestoneData::encounteredMobs, event.getEntity().getType());
        }
    }

    @SubscribeEvent
    public static void onItemAcquired(InventoryChangedEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer player)
        {
            for (ItemStack item : event.getInventory().items)
            {
                applyMatchingMilestone(player.serverLevel(), MilestoneData::acquiredItems, item.getItem());
            }
        }
    }

    @SubscribeEvent
    public static void onBlockMined(LivingDestroyBlockEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            applyMatchingMilestone(player.serverLevel(), MilestoneData::minedBlocks, event.getState().getBlock());
        }
    }

    private static <T> void applyMatchingMilestone(ServerLevel level, Function<MilestoneData, List<T>> getter, T value)
    {
        Registry<MilestoneData> milestoneRegistry = level.registryAccess().registryOrThrow(ModRegistries.MILESTONE_DATA);

        Class<? super T> baseClass = getBaseClass(value);
        if (TESTED_OBJECTS.put(baseClass, value))
        {
            for (MilestoneData milestoneData : milestoneRegistry)
            {
                if (getter.apply(milestoneData).contains(value))
                {
                    LevelScalingData scalingData = LevelScalingData.get(level);
                    scalingData.setScale(Math.max(scalingData.scale(), milestoneData.scale()));
                }
            }
        }
    }

    private static <T> Class<? super T> getBaseClass(T obj)
    {
        if (obj instanceof ResourceLocation) return (Class) ResourceLocation.class;
        if (obj instanceof ResourceKey<?>) return (Class) ResourceKey.class;
        if (obj instanceof EntityType<?>) return (Class) EntityType.class;
        if (obj instanceof Item) return (Class) Item.class;
        if (obj instanceof Block) return (Class) Block.class;
        return null;
    }
}
