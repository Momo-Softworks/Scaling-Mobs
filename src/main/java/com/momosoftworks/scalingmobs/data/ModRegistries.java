package com.momosoftworks.scalingmobs.data;

import com.momosoftworks.scalingmobs.ScalingMobs;
import com.momosoftworks.scalingmobs.data.config.MilestoneData;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ModRegistries
{
    public static final ResourceKey<Registry<MilestoneData>> MILESTONE_DATA = ResourceKey.createRegistryKey(new ResourceLocation(ScalingMobs.MOD_ID, "milestone"));
}
