package com.momosoftworks.scalingmobs.data;

import com.momosoftworks.scalingmobs.ScalingMobs;
import com.momosoftworks.scalingmobs.data.config.MilestoneData;
import com.momosoftworks.scalingmobs.data.config.MobModifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ModRegistries
{
    public static final ResourceKey<Registry<MilestoneData>> MILESTONE = ResourceKey.createRegistryKey(new ResourceLocation(ScalingMobs.MOD_ID, "milestone"));
    public static final ResourceKey<Registry<MobModifier>> MOB_MODIFIER = ResourceKey.createRegistryKey(new ResourceLocation(ScalingMobs.MOD_ID, "mob_modifier"));
}
