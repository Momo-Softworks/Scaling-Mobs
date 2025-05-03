package com.momosoftworks.scalingmobs.events;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Either;
import com.momosoftworks.scalingmobs.ScalingMobs;
import com.momosoftworks.scalingmobs.api.event.InventoryChangedEvent;
import com.momosoftworks.scalingmobs.api.event.LivingFindTargetEvent;
import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import com.momosoftworks.scalingmobs.data.ModRegistries;
import com.momosoftworks.scalingmobs.data.config.MilestoneData;
import com.momosoftworks.scalingmobs.data.save_data.LevelScalingData;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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
            applyMilestone(level, event.getAdvancement().getId(), MilestoneData::advancements);
        }
    }

    @SubscribeEvent
    public static void onEnterDimension(EntityTravelToDimensionEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            Holder<DimensionType> dimension = player.server.getLevel(event.getDimension()).dimensionTypeRegistration();
            applyTaggableHolderMilestone(player.serverLevel(), dimension, MilestoneData::dimensions);
        }
    }

    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event)
    {
        LivingEntity target = event.getEntity();
        if (target.getLastHurtByMob() instanceof ServerPlayer player)
        {
            applyTaggableMilestone(player.serverLevel(), target.getType(), MilestoneData::killedMobs, ForgeRegistries.ENTITY_TYPES);
        }
    }

    @SubscribeEvent
    public static void onHurtByMob(LivingHurtEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player
        && event.getSource().getEntity() instanceof LivingEntity entity)
        {
            applyTaggableMilestone(player.serverLevel(), entity.getType(), MilestoneData::encounteredMobs, ForgeRegistries.ENTITY_TYPES);
        }
    }

    @SubscribeEvent
    public static void onAttackMob(LivingHurtEvent event)
    {
        if (event.getSource().getEntity() instanceof ServerPlayer player)
        {
            applyTaggableMilestone(player.serverLevel(), event.getEntity().getType(), MilestoneData::encounteredMobs, ForgeRegistries.ENTITY_TYPES);
        }
    }

    @SubscribeEvent
    public static void onTargetedByMob(LivingFindTargetEvent event)
    {
        if (event.getTarget() instanceof ServerPlayer player)
        {
            applyTaggableMilestone(player.serverLevel(), event.getEntity().getType(), MilestoneData::encounteredMobs, ForgeRegistries.ENTITY_TYPES);
        }
    }

    @SubscribeEvent
    public static void onItemAcquired(InventoryChangedEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer player)
        {
            for (ItemStack item : event.getInventory().items)
            {
                applyTaggableMilestone(player.serverLevel(), item.getItem(), MilestoneData::acquiredItems, ForgeRegistries.ITEMS);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockMined(LivingDestroyBlockEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            applyTaggableMilestone(player.serverLevel(), event.getState().getBlock(), MilestoneData::minedBlocks, ForgeRegistries.BLOCKS);
        }
    }

    private static <T> void applyTaggableMilestone(ServerLevel level, T value, Function<MilestoneData, List<Either<TagKey<T>, T>>> getter, IForgeRegistry<T> registry)
    {
        applyMilestoneInternal(level, getter, list -> list.stream().anyMatch(either -> either.map(tag -> registry.tags().getTag(tag).contains(value),
                                                                                                  val -> val.equals(value))), value);
    }

    private static <T> void applyTaggableHolderMilestone(ServerLevel level, Holder<T> value, Function<MilestoneData, List<Either<TagKey<T>, Holder<T>>>> getter)
    {
        applyMilestoneInternal(level, getter, list -> list.stream().anyMatch(either -> either.map(tag -> value.is(tag),
                                                                                                  holder -> holder.value().equals(value))), value);
    }

    private static <T> void applyMilestone(ServerLevel level, T value, Function<MilestoneData, List<T>> getter)
    {   applyMilestoneInternal(level, getter, list -> list.contains(value), value);
    }

    private static <L, T> void applyMilestoneInternal(ServerLevel level, Function<MilestoneData, List<L>> getter, Predicate<List<L>> listTester, T value)
    {
        Registry<MilestoneData> milestoneRegistry = level.registryAccess().registryOrThrow(ModRegistries.MILESTONE);

        Class<?> baseClass = getBaseClass(value);
        if (baseClass == null)
        {   ScalingMobs.LOGGER.error("{} is not a valid objet type for milestones", value.getClass().getSimpleName());
            return;
        }
        if (TESTED_OBJECTS.put(baseClass, value))
        {
            for (Holder<MilestoneData> milestoneData : milestoneRegistry.holders().toList())
            {
                if (listTester.test(getter.apply(milestoneData.value())))
                {
                    LevelScalingData scalingData = LevelScalingData.get(level);
                    if (scalingData.addMilestone(milestoneData))
                    {   scalingData.setScale(Math.max(scalingData.scale(), milestoneData.value().scale()));
                    }
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
