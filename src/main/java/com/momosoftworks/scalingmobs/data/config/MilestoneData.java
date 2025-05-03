package com.momosoftworks.scalingmobs.data.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.momosoftworks.scalingmobs.util.ConfigHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public record MilestoneData(List<ResourceLocation> advancements, List<Either<TagKey<DimensionType>, Holder<DimensionType>>> dimensions,
                            List<Either<TagKey<EntityType<?>>, EntityType<?>>> killedMobs,
                            List<Either<TagKey<EntityType<?>>, EntityType<?>>> encounteredMobs,
                            List<Either<TagKey<Item>, Item>> acquiredItems, List<Either<TagKey<Block>, Block>> minedBlocks,
                            double scale)
{
    public static final Codec<MilestoneData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("advancements", List.of()).forGetter(MilestoneData::advancements),
            ConfigHelper.tagOrHolderCodec(Registries.DIMENSION_TYPE, DimensionType.CODEC).listOf().optionalFieldOf("dimensions", List.of()).forGetter(MilestoneData::dimensions),
            ConfigHelper.tagOrBuiltinCodec(Registries.ENTITY_TYPE, ForgeRegistries.ENTITY_TYPES).listOf().optionalFieldOf("killed_mobs", List.of()).forGetter(MilestoneData::killedMobs),
            ConfigHelper.tagOrBuiltinCodec(Registries.ENTITY_TYPE, ForgeRegistries.ENTITY_TYPES).listOf().optionalFieldOf("encountered_mobs", List.of()).forGetter(MilestoneData::encounteredMobs),
            ConfigHelper.tagOrBuiltinCodec(Registries.ITEM, ForgeRegistries.ITEMS).listOf().optionalFieldOf("acquired_items", List.of()).forGetter(MilestoneData::acquiredItems),
            ConfigHelper.tagOrBuiltinCodec(Registries.BLOCK, ForgeRegistries.BLOCKS).listOf().optionalFieldOf("mined_blocks", List.of()).forGetter(MilestoneData::minedBlocks),
            Codec.DOUBLE.fieldOf("scale").forGetter(MilestoneData::scale)
    ).apply(instance, MilestoneData::new));
}
