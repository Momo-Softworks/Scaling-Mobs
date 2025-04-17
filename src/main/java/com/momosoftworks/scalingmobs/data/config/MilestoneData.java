package com.momosoftworks.scalingmobs.data.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public record MilestoneData(List<ResourceLocation> advancements, List<ResourceKey<Level>> dimensions,
                            List<EntityType<?>> killedMobs, List<EntityType<?>> encounteredMobs,
                            List<Item> acquiredItems, List<Block> minedBlocks,
                            double scale)
{
    public static final Codec<MilestoneData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("advancements", List.of()).forGetter(MilestoneData::advancements),
            ResourceKey.codec(Registries.DIMENSION).listOf().optionalFieldOf("dimensions", List.of()).forGetter(MilestoneData::dimensions),
            ForgeRegistries.ENTITY_TYPES.getCodec().listOf().optionalFieldOf("killed_mobs", List.of()).forGetter(MilestoneData::killedMobs),
            ForgeRegistries.ENTITY_TYPES.getCodec().listOf().optionalFieldOf("encountered_mobs", List.of()).forGetter(MilestoneData::encounteredMobs),
            ForgeRegistries.ITEMS.getCodec().listOf().optionalFieldOf("acquired_items", List.of()).forGetter(MilestoneData::acquiredItems),
            ForgeRegistries.BLOCKS.getCodec().listOf().optionalFieldOf("mined_blocks", List.of()).forGetter(MilestoneData::minedBlocks),
            Codec.DOUBLE.fieldOf("scale").forGetter(MilestoneData::scale)
    ).apply(instance, MilestoneData::new));
}
