package com.momosoftworks.scalingmobs.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;

public class ConfigHelper
{
    public static <T> Codec<Either<TagKey<T>, T>> tagOrBuiltinCodec(ResourceKey<Registry<T>> vanillaRegistry, IForgeRegistry<T> forgeRegistry)
    {
        return Codec.either(Codec.STRING.comapFlatMap(str ->
                    {
                        if (!str.startsWith("#"))
                        {   return DataResult.error(() -> "Not a tag key: " + str);
                        }
                        ResourceLocation itemLocation = new ResourceLocation(str.replace("#", ""));
                        return DataResult.success(TagKey.create(vanillaRegistry, itemLocation));
                    },
                    key -> "#" + key.location()),
               forgeRegistry.getCodec());
    }

    public static <T> Codec<Either<TagKey<T>, Holder<T>>> tagOrHolderCodec(ResourceKey<Registry<T>> vanillaRegistry, Codec<Holder<T>> codec)
    {
        return Codec.either(Codec.STRING.comapFlatMap(
                   str ->
                   {
                       if (!str.startsWith("#"))
                       {   return DataResult.error(() -> "Not a tag key: " + str);
                       }
                       ResourceLocation itemLocation = new ResourceLocation(str.replace("#", ""));
                       return DataResult.success(TagKey.create(vanillaRegistry, itemLocation));
                   },
                   key -> "#" + key.location()),
               codec);
    }
}
