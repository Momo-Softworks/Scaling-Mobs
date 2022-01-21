package net.momostudios.scalingmobs.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScalingMobsConfig
{
    private static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ScalingMobsConfig INSTANCE = new ScalingMobsConfig();

    private static final ForgeConfigSpec.DoubleValue mobScaleRate;
    private static final ForgeConfigSpec.DoubleValue mobStatsBase;
    private static final ForgeConfigSpec.DoubleValue maxScaling;

    private static final ForgeConfigSpec.DoubleValue mobPiercingRate;
    private static final ForgeConfigSpec.DoubleValue mobPiercingBase;
    private static final ForgeConfigSpec.DoubleValue maxPiercing;

    public static final ForgeConfigSpec.IntValue mobsStopBurningDay;

    private static final ForgeConfigSpec.BooleanValue exponential;

    static
    {
        exponential = BUILDER
                .comment("If true, mob stats will increase exponentialally")
                .define("exponential", false);

        mobScaleRate = BUILDER
                .comment("The percent amount that hostile mobs' stats increase per day")
                .defineInRange("mobScaleRate", 0.03, 0.0, Double.MAX_VALUE);
        mobStatsBase = BUILDER
                .comment("The percent amount that is added to hostile mobs' base stats")
                .defineInRange("mobStatsBase", 1.0, 0.0, Double.MAX_VALUE);
        maxScaling = BUILDER
                .comment("The maximum amount that hostile mobs' stats can scale to")
                .defineInRange("maxScaling", Double.MAX_VALUE, 0.0, Double.MAX_VALUE);

        mobPiercingRate = BUILDER
                .comment("The percent amount of increase to mobs' damage that ignores armor per day")
                .defineInRange("mobPiercingRate", 0.01, 0.0, Double.MAX_VALUE);
        mobPiercingBase = BUILDER
                .comment("The percent amount of mobs' damage that ignores armor")
                .defineInRange("mobPiercingBase", 0.0, 0.0, Double.MAX_VALUE);
        maxPiercing = BUILDER
                .comment("The maximum amount of increase to mobs' damage that ignores armor")
                .defineInRange("maxPiercing", Double.MAX_VALUE, 0.0, Double.MAX_VALUE);

        mobsStopBurningDay = BUILDER
                .comment("The number of days before mobs stop burning")
                .defineInRange("mobsStopBurningDay", 3, 0, Integer.MAX_VALUE);

        SPEC = BUILDER.build();
    }

    // Getters
    public boolean areStatsExponential()
    {
        return exponential.get();
    }
    public double getMobScaleRate()
    {
        return mobScaleRate.get();
    }
    public double getMobStatsBase()
    {
        return mobStatsBase.get();
    }
    public double getMaxScaling()
    {
        return maxScaling.get();
    }
    public double getPiercingRate()
    {
        return mobPiercingRate.get();
    }
    public double getMaxPiercing()
    {
        return maxPiercing.get();
    }
    public double getPiercingBase()
    {
        return mobPiercingBase.get();
    }
    public int getMobsStopBurningDay()
    {
        return mobsStopBurningDay.get();
    }

    // Setters
    public void setExponentialStats(boolean log)
    {
        exponential.set(log);
    }
    public void setMobScaleRate(double rate)
    {
        mobScaleRate.set(rate);
    }
    public void setMobStatsBase(double base)
    {
        mobStatsBase.set(base);
    }
    public void setMaxScaling(double max)
    {
        maxScaling.set(max);
    }
    public void setPiercingRate(double rate)
    {
        mobPiercingRate.set(rate);
    }
    public void setPiercingBase(double base)
    {
        mobPiercingBase.set(base);
    }
    public void setMaxPiercing(double max)
    {
        maxPiercing.set(max);
    }
    public void setMobsStopBurningDay(int day)
    {
        mobsStopBurningDay.set(day);
    }

    public static void setup()
    {
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path csConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "scaling_mobs");

        // Create the config folder
        try
        {
            Files.createDirectory(csConfigPath);
        }
        catch (Exception e)
        {
            // Do nothing
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "scaling_mobs/main.toml");
    }

    public static ScalingMobsConfig getInstance()
    {
        return INSTANCE;
    }

    public void save() {
        SPEC.save();
    }
}
