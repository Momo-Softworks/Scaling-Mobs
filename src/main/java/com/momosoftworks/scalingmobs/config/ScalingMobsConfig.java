package com.momosoftworks.scalingmobs.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ScalingMobsConfig
{
    private static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue SCALING_SPEED;

    public static final ForgeConfigSpec.DoubleValue MOB_HEALTH_RATE;
    public static final ForgeConfigSpec.DoubleValue MOB_HEALTH_BASE;
    public static final ForgeConfigSpec.DoubleValue MOB_HEALTH_MAX;

    public static final ForgeConfigSpec.DoubleValue MOB_DAMAGE_RATE;
    public static final ForgeConfigSpec.DoubleValue MOB_DAMAGE_BASE;
    public static final ForgeConfigSpec.DoubleValue MOB_DAMAGE_MAX;

    public static final ForgeConfigSpec.DoubleValue MOB_SPEED_RATE;
    public static final ForgeConfigSpec.DoubleValue MOB_SPEED_BASE;
    public static final ForgeConfigSpec.DoubleValue MOB_SPEED_MAX;

    public static final ForgeConfigSpec.DoubleValue ARMOR_PIERCING_RATE;
    public static final ForgeConfigSpec.DoubleValue ARMOR_PIERCING_BASE;
    public static final ForgeConfigSpec.DoubleValue ARMOR_PIERCING_MAX;

    public static final ForgeConfigSpec.DoubleValue MOB_DROPS_RATE;
    public static final ForgeConfigSpec.DoubleValue MOB_DROPS_BASE;
    public static final ForgeConfigSpec.DoubleValue MOB_DROPS_MAX;

    public static final ForgeConfigSpec.IntValue STOP_BURNING_DAY;

    public static final ForgeConfigSpec.BooleanValue EXPONENTIAL_SCALING;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_BLACKLIST;

    static
    {
        BUILDER.push("Misc");

        SCALING_SPEED = BUILDER
                .comment("The amount by which which mobs scale, per Minecraft day")
                .defineInRange("Scaling Speed", 1, 0.0, Double.POSITIVE_INFINITY);

        EXPONENTIAL_SCALING = BUILDER
                .comment("If true, mob stats will increase exponentially")
                .define("Use Exponential Scaling", false);

        STOP_BURNING_DAY = BUILDER
                .comment("After this day in the Minecraft world, all hostile mobs will be immune to burning in daylight")
                .defineInRange("Mobs Stop Burning on Day", 7, 0, Integer.MAX_VALUE);

        MOB_WHITELIST = BUILDER
                .comment("A list of mobs that will scale, even if they're not monsters")
                .defineList("Mob Whitelist", List.of(),
                            element -> element instanceof String);  BUILDER.pop();

        MOB_BLACKLIST = BUILDER
                .comment("A list of mobs that will not scale, even if they're monsters")
                .defineList("Mob Blacklist", List.of(),
                            element -> element instanceof String);

        BUILDER.push("Health");

        MOB_HEALTH_RATE = BUILDER
                .comment("The decimal amount that hostile mobs' health increase per day")
                .defineInRange("Health Scale Rate", 0.03, 0.0, Double.POSITIVE_INFINITY);
        MOB_HEALTH_BASE = BUILDER
                .comment("The decimal amount of the mobs' base health in the beginning")
                .defineInRange("Mob Health Base", 0.0, 0.0, Double.POSITIVE_INFINITY);
        MOB_HEALTH_MAX = BUILDER
                .comment("The maximum amount that hostile mobs' damage can scale to")
                .defineInRange("Max Scaled Health", Double.POSITIVE_INFINITY, 0.0, Double.POSITIVE_INFINITY);

        BUILDER.pop();
        BUILDER.push("Damage");

        MOB_DAMAGE_RATE = BUILDER
                .comment("The decimal amount that hostile mobs' damage increase per day")
                .defineInRange("Damage Scale Rate", 0.03, 0.0, Double.POSITIVE_INFINITY);
        MOB_DAMAGE_BASE = BUILDER
                .comment("The decimal amount of the mobs' base damage in the beginning")
                .defineInRange("Mob Damage Base", 1.0, 0.0, Double.POSITIVE_INFINITY);
        MOB_DAMAGE_MAX = BUILDER
                .comment("The maximum amount that hostile mobs' damage can scale to")
                .defineInRange("Max Scaled Damage", Double.POSITIVE_INFINITY, 0.0, Double.POSITIVE_INFINITY);

        BUILDER.pop();
        BUILDER.push("Speed");

        MOB_SPEED_RATE = BUILDER
                .comment("The decimal amount that hostile mobs' speed increase per day")
                .defineInRange("Speed Scale Rate", 0.005, 0.0, Double.POSITIVE_INFINITY);

        MOB_SPEED_BASE = BUILDER
                .comment("The decimal amount of the mobs' base speed in the beginning")
                .defineInRange("Mob Speed Base", 1.0, 0.0, Double.POSITIVE_INFINITY);

        MOB_SPEED_MAX = BUILDER
                .comment("The maximum amount that hostile mobs' speed can scale to")
                .defineInRange("Max Scaled Speed", 1.5, 0.0, Double.POSITIVE_INFINITY);

        BUILDER.pop();
        BUILDER.push("Armor Piercing");

        ARMOR_PIERCING_RATE = BUILDER
                .comment("The decimal amount of increase to mobs' damage that ignores armor per day")
                .defineInRange("Armor Piercing Scale Rate", 0.01, 0.0, Double.POSITIVE_INFINITY);
        ARMOR_PIERCING_BASE = BUILDER
                .comment("The decimal amount of mobs' damage that ignores armor in the beginning")
                .defineInRange("Armor Piercing Base", 0.1, 0.0, Double.POSITIVE_INFINITY);
        ARMOR_PIERCING_MAX = BUILDER
                .comment("The maximum amount of increase to mobs' damage that ignores armor")
                .defineInRange("Max Scaled Armor Piercing", 1.0, 0.0, Double.POSITIVE_INFINITY);

        BUILDER.pop();
        BUILDER.push("Drops");

        MOB_DROPS_RATE = BUILDER
                .comment("The decimal amount of increase to mobs' drops per day")
                .defineInRange("Mob Drops Scaling Rate", 0.02, 0.0, Double.POSITIVE_INFINITY);
        MOB_DROPS_BASE = BUILDER
                .comment("The decimal amount of mobs' drops in the beginning")
                .defineInRange("Mob Drops Base", 1.0, 0.0, Double.POSITIVE_INFINITY);
        MOB_DROPS_MAX = BUILDER
                .comment("The maximum amount of increase to mobs' drops")
                .defineInRange("Max Scaled Mob Drops", 400, 0.0, Double.POSITIVE_INFINITY);

        BUILDER.pop();

        SPEC = BUILDER.build();
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

    public void save() {
        SPEC.save();
    }
}
