package com.momosoftworks.scalingmobs.data;

import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public enum ScalableStat
{
    DAMAGE(ScalingMobsConfig.MOB_DAMAGE_BASE, ScalingMobsConfig.MOB_DAMAGE_RATE, ScalingMobsConfig.MOB_DAMAGE_MAX),
    HEALTH(ScalingMobsConfig.MOB_HEALTH_BASE, ScalingMobsConfig.MOB_HEALTH_RATE, ScalingMobsConfig.MOB_HEALTH_MAX),
    SPEED(ScalingMobsConfig.MOB_SPEED_BASE, ScalingMobsConfig.MOB_SPEED_RATE, ScalingMobsConfig.MOB_SPEED_MAX),
    ARMOR_PIERCING(ScalingMobsConfig.ARMOR_PIERCING_BASE, ScalingMobsConfig.ARMOR_PIERCING_RATE, ScalingMobsConfig.ARMOR_PIERCING_MAX),
    DROPS(ScalingMobsConfig.MOB_DROPS_BASE, ScalingMobsConfig.MOB_DROPS_RATE, ScalingMobsConfig.MOB_DROPS_MAX),
    EXPERIENCE(ScalingMobsConfig.MOB_XP_BASE, ScalingMobsConfig.MOB_XP_RATE, ScalingMobsConfig.MOB_XP_MAX);

    public final double base;
    public final double rate;
    public final double max;

    ScalableStat(ForgeConfigSpec.DoubleValue base, ForgeConfigSpec.DoubleValue rate, ForgeConfigSpec.DoubleValue max)
    {
        this.base = base.get();
        this.rate = rate.get();
        this.max = max.get();
    }

    public double base()
    {   return base;
    }
    public double rate()
    {   return rate;
    }
    public double max()
    {   return max;
    }

    public double getMultiplier(double scale)
    {   return Math.min(base + rate * scale, max);
    }
}
