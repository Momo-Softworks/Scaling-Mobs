package com.momosoftworks.scalingmobs.data.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.momosoftworks.scalingmobs.data.save_data.LevelScalingData;
import com.momosoftworks.scalingmobs.util.ConfigHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MobModifier(List<Either<TagKey<EntityType<?>>, EntityType<?>>> entities, Map<Attribute, Double> attributes,
                          List<Either<TagKey<Biome>, Holder<Biome>>> biomeBlacklist,
                          List<Either<TagKey<DimensionType>, Holder<DimensionType>>> dimensionBlacklist,
                          int distanceFromSpawn, double minScale, double maxScale, boolean disable)
{
    public static final Codec<MobModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ConfigHelper.tagOrBuiltinCodec(Registries.ENTITY_TYPE, ForgeRegistries.ENTITY_TYPES).listOf().fieldOf("entities").forGetter(MobModifier::entities),
            Codec.unboundedMap(ForgeRegistries.ATTRIBUTES.getCodec(), Codec.DOUBLE).optionalFieldOf("attributes", new HashMap<>()).forGetter(MobModifier::attributes),
            ConfigHelper.tagOrHolderCodec(Registries.BIOME, Biome.CODEC).listOf().optionalFieldOf("biome_blacklist", List.of()).forGetter(MobModifier::biomeBlacklist),
            ConfigHelper.tagOrHolderCodec(Registries.DIMENSION_TYPE, DimensionType.CODEC).listOf().optionalFieldOf("dimension_blacklist", List.of()).forGetter(MobModifier::dimensionBlacklist),
            Codec.INT.optionalFieldOf("distance_from_spawn", 0).forGetter(MobModifier::distanceFromSpawn),
            Codec.DOUBLE.optionalFieldOf("min_scale", 0d).forGetter(MobModifier::minScale),
            Codec.DOUBLE.optionalFieldOf("max_scale", Double.MAX_VALUE).forGetter(MobModifier::maxScale),
            Codec.BOOL.optionalFieldOf("disable", false).forGetter(MobModifier::disable)
    ).apply(instance, MobModifier::new));

    public boolean hasEntity(LivingEntity entity)
    {
        EntityType<?> type = entity.getType();
        for (Either<TagKey<EntityType<?>>, EntityType<?>> either : this.entities)
        {
            if (either.map(type::is, type::equals))
            {   return true;
            }
        }
        return false;
    }

    public boolean canEntitySpawn(LivingEntity entity)
    {
        if (this.disable) return false;
        if (this.distanceFromSpawn > 0
        && entity.blockPosition().distSqr(entity.level().getSharedSpawnPos()) < this.distanceFromSpawn * this.distanceFromSpawn)
        {   return true;
        }
        if (!this.biomeBlacklist.isEmpty())
        {
            Holder<Biome> entityBiome = entity.level().getBiome(entity.blockPosition());
            for (Either<TagKey<Biome>, Holder<Biome>> either : this.biomeBlacklist)
            {
                if (either.map(entityBiome::is, entityBiome::equals))
                {   return false;
                }
            }
        }
        if (!this.dimensionBlacklist.isEmpty())
        {
            Holder<DimensionType> entityDimension = entity.level().dimensionTypeRegistration();
            for (Either<TagKey<DimensionType>, Holder<DimensionType>> either : this.dimensionBlacklist)
            {
                if (either.map(entityDimension::is, entityDimension::equals))
                {   return false;
                }
            }
        }
        if (entity.level() instanceof ServerLevel level)
        {
            double scale = LevelScalingData.get(level).scale();
            if (scale < this.minScale || scale > this.maxScale)
            {   return false;
            }
        }
        return true;
    }
}
