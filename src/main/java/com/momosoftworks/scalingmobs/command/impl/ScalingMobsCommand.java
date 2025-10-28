package com.momosoftworks.scalingmobs.command.impl;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.momosoftworks.scalingmobs.command.BaseCommand;
import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import com.momosoftworks.scalingmobs.data.ScalableStat;
import com.momosoftworks.scalingmobs.data.save_data.LevelScalingData;
import com.momosoftworks.scalingmobs.events.MonsterEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeConfigSpec;

import java.text.DecimalFormat;

public class ScalingMobsCommand extends BaseCommand
{
    public ScalingMobsCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return builder
                .then(Commands.literal("health")
                        .then(Commands.literal("rate")
                                .then(Commands.argument("set", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setRateConfig(source.getSource(), ScalingMobsConfig.MOB_HEALTH_RATE, "health", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getRateConfig(source.getSource(), ScalingMobsConfig.MOB_HEALTH_RATE, "health"))
                                )
                        )
                        .then(Commands.literal("base")
                                .then(Commands.argument("set", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setBaseConfig(source.getSource(), ScalingMobsConfig.MOB_HEALTH_BASE, "health", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getBaseConfig(source.getSource(), ScalingMobsConfig.MOB_HEALTH_BASE, "health"))
                                )
                        )
                        .then(Commands.literal("max")
                                .then(Commands.argument("set", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setMaxConfig(source.getSource(), ScalingMobsConfig.MOB_HEALTH_MAX, "health", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getMaxConfig(source.getSource(), ScalingMobsConfig.MOB_HEALTH_MAX, "health"))
                                )
                        )
                )
                .then(Commands.literal("damage")
                        .then(Commands.literal("rate")
                                .then(Commands.argument("set", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setRateConfig(source.getSource(), ScalingMobsConfig.MOB_DAMAGE_RATE, "damage", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getRateConfig(source.getSource(), ScalingMobsConfig.MOB_DAMAGE_RATE, "damage"))
                                )
                        )
                        .then(Commands.literal("base")
                                .then(Commands.argument("set", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setBaseConfig(source.getSource(), ScalingMobsConfig.MOB_DAMAGE_BASE, "damage", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getBaseConfig(source.getSource(), ScalingMobsConfig.MOB_DAMAGE_BASE, "damage"))
                                )
                        )
                        .then(Commands.literal("max")
                                .then(Commands.argument("set", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE))
                                        .executes(source -> setMaxConfig(source.getSource(), ScalingMobsConfig.MOB_DAMAGE_MAX, "damage", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getMaxConfig(source.getSource(), ScalingMobsConfig.MOB_DAMAGE_MAX, "damage"))
                                )
                        )
                )
                .then(Commands.literal("piercing")
                        .then(Commands.literal("rate")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setRateConfig(source.getSource(), ScalingMobsConfig.ARMOR_PIERCING_RATE, "piercing", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getRateConfig(source.getSource(), ScalingMobsConfig.ARMOR_PIERCING_RATE, "piercing"))
                                )
                        )
                        .then(Commands.literal("base")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setBaseConfig(source.getSource(), ScalingMobsConfig.ARMOR_PIERCING_BASE, "piercing", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getBaseConfig(source.getSource(), ScalingMobsConfig.ARMOR_PIERCING_BASE, "piercing"))
                                )
                        )
                        .then(Commands.literal("max")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setMaxConfig(source.getSource(), ScalingMobsConfig.ARMOR_PIERCING_MAX, "piercing", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getMaxConfig(source.getSource(), ScalingMobsConfig.ARMOR_PIERCING_MAX, "piercing"))
                                )
                        )
                )
                .then(Commands.literal("drops")
                        .then(Commands.literal("rate")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setRateConfig(source.getSource(), ScalingMobsConfig.MOB_DROPS_RATE, "drops", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getRateConfig(source.getSource(), ScalingMobsConfig.MOB_DROPS_RATE, "drops"))
                                )
                        )
                        .then(Commands.literal("base")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setBaseConfig(source.getSource(), ScalingMobsConfig.MOB_DROPS_BASE, "drops", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getBaseConfig(source.getSource(), ScalingMobsConfig.MOB_DROPS_BASE, "drops"))
                                )
                        )
                        .then(Commands.literal("max")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setMaxConfig(source.getSource(), ScalingMobsConfig.MOB_DROPS_MAX, "drops", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getMaxConfig(source.getSource(), ScalingMobsConfig.MOB_DROPS_MAX, "drops"))
                                )
                        )
                )
                .then(Commands.literal("xp")
                        .then(Commands.literal("rate")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setRateConfig(source.getSource(), ScalingMobsConfig.MOB_XP_RATE, "xp", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getRateConfig(source.getSource(), ScalingMobsConfig.MOB_XP_RATE, "xp"))
                                )
                        )
                        .then(Commands.literal("base")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setBaseConfig(source.getSource(), ScalingMobsConfig.MOB_XP_BASE, "xp", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getBaseConfig(source.getSource(), ScalingMobsConfig.MOB_XP_BASE, "xp"))
                                )
                        )
                        .then(Commands.literal("max")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setMaxConfig(source.getSource(), ScalingMobsConfig.MOB_XP_MAX, "xp", DoubleArgumentType.getDouble(source, "amount")))
                                )
                                .then(Commands.literal("get")
                                        .executes(source -> getMaxConfig(source.getSource(), ScalingMobsConfig.MOB_XP_MAX, "xp"))
                                )
                        )
                )
                .then(Commands.literal("mode")
                        .then(Commands.literal("exponential").requires(source -> source.hasPermission(2))
                                .executes(source -> setExponential(source.getSource(), true))
                        )
                        .then(Commands.literal("linear").requires(source -> source.hasPermission(2))
                                .executes(source -> setExponential(source.getSource(), false))
                        )
                        .then(Commands.literal("get")
                                .executes(source -> getExponential(source.getSource()))
                        )
                )
                .then(Commands.literal("burnDay")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                .executes(source -> setBurnDay(source.getSource(), IntegerArgumentType.getInteger(source, "amount")))
                        )
                        .then(Commands.literal("get")
                                .executes(source -> getBurnDay(source.getSource()))
                        )
                )
                .then(Commands.literal("scale")
                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE)).requires(source -> source.hasPermission(2))
                                        .executes(source -> setScale(source.getSource(), DoubleArgumentType.getDouble(source, "amount")))
                                )
                        )
                        .then(Commands.literal("get")
                                .executes(source -> getScale(source.getSource()))
                        )
                )
                .then(Commands.literal("get")
                        .executes(source -> getAll(source.getSource()))
                );
    }

    private static Component getRateMessage(String stat, double rate)
    {   return Component.literal(String.format("Set the rate of mob %s scaling to +%s%% per day", stat, formatDouble(rate * 100)));
    }

    private static Component getBaseMessage(String stat, double base)
    {   return Component.literal(String.format("Set the base %s multiplier of all mobs to %s%%", stat, formatDouble((1 + base) * 100)));
    }

    private static Component getMaxMessage(String stat, double max)
    {   return Component.literal(String.format("Set the maximum %s scaling of all mobs to %s%%", stat, formatDouble((1 + max) * 100)));
    }

    /**
     * Set Commands
     */
    static int setRateConfig(CommandSourceStack source, ForgeConfigSpec.DoubleValue config, String stat, double rate)
    {
        config.set(rate);
        source.sendSuccess(() -> getRateMessage(stat, rate), true);

        return Command.SINGLE_SUCCESS;
    }

    static int setBaseConfig(CommandSourceStack source, ForgeConfigSpec.DoubleValue config, String stat, double base)
    {
        config.set(base);
        source.sendSuccess(() -> getBaseMessage(stat, base), true);

        return Command.SINGLE_SUCCESS;
    }

    static int setMaxConfig(CommandSourceStack source, ForgeConfigSpec.DoubleValue config, String stat, double max)
    {
        config.set(max);
        source.sendSuccess(() -> getMaxMessage(stat, max), true);

        return Command.SINGLE_SUCCESS;
    }

    static int setExponential(CommandSourceStack source, boolean exponential)
    {
        ScalingMobsConfig.EXPONENTIAL_SCALING.set(exponential);

        Component message = Component.literal(String.format("Set mob damage scaling mode to %s", ScalingMobsConfig.EXPONENTIAL_SCALING.get() ? "exponential" : "linear"));
        source.sendSuccess(() -> message, true);

        return Command.SINGLE_SUCCESS;
    }

    static int setBurnDay(CommandSourceStack source, int day)
    {
        ScalingMobsConfig.STOP_BURNING_DAY.set(day);

        Component message = Component.literal(String.format("Mobs will now stop burning after day %s", ScalingMobsConfig.STOP_BURNING_DAY.get()));
        source.sendSuccess(() -> message, true);

        return Command.SINGLE_SUCCESS;
    }

    static int setScale(CommandSourceStack source, double scale)
    {
        LevelScalingData.get(source.getLevel()).setScale(scale);
        for (Entity entity : source.getLevel().getAllEntities())
        {
            if (entity instanceof LivingEntity living && MonsterEvents.isScalingMob(entity))
            {   MonsterEvents.initializeAttributeModifiers(living);
            }
        }

        Component message = Component.literal(String.format("Set mob scaling factor to %s", formatDouble(scale)));
        source.sendSuccess(() -> message, true);

        return Command.SINGLE_SUCCESS;
    }
    
    /**
     * Get Commands
     */
    static int getRateConfig(CommandSourceStack source, ForgeConfigSpec.DoubleValue config, String stat)
    {
        Component message = Component.literal(String.format("The rate of mob %s scaling is currently %s", stat, formatDouble(config.get())));
        source.sendSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }

    static int getBaseConfig(CommandSourceStack source, ForgeConfigSpec.DoubleValue config, String stat)
    {
        Component message = Component.literal(String.format("The base %s multiplier of all mobs is currently %s", stat, formatDouble(config.get())));
        source.sendSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }

    static int getMaxConfig(CommandSourceStack source, ForgeConfigSpec.DoubleValue config, String stat)
    {
        Component message = Component.literal(String.format("The maximum %s scaling of all mobs is currently %s", stat, formatDouble(config.get())));
        source.sendSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }

    static int getExponential(CommandSourceStack source)
    {
        Component message = Component.literal("Mob damage scaling is currently " + (ScalingMobsConfig.EXPONENTIAL_SCALING.get() ? "exponential" : "linear"));
        source.sendSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }

    static int getBurnDay(CommandSourceStack source)
    {
        int stopBurningDay = ScalingMobsConfig.STOP_BURNING_DAY.get();
        boolean inPast = source.getLevel().dayTime() / 24000 > stopBurningDay;
        Component message = inPast ? Component.literal("Mobs stopped burning on day " + stopBurningDay)
                                   : Component.literal("Mobs will stop burning after day " + stopBurningDay);
        source.sendSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }

    static int getScale(CommandSourceStack source)
    {
        String scale = formatDouble(LevelScalingData.get(source.getLevel()).scale());
        Component message = Component.literal("Mob scaling factor is currently " + scale);
        source.sendSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }

    static int getAll(CommandSourceStack source)
    {
        double scale = LevelScalingData.get(source.getLevel()).scale();

        source.sendSystemMessage(Component.literal("Current damage scaling: " +
                formatDouble(100 + ScalableStat.DAMAGE.getMultiplier(scale) * 100) + "%"));

        source.sendSystemMessage(Component.literal("Current health scaling: " +
                formatDouble(100 + ScalableStat.HEALTH.getMultiplier(scale) * 100) + "%"));

        source.sendSystemMessage(Component.literal("Current piercing scaling: " +
                formatDouble(100 + ScalableStat.ARMOR_PIERCING.getMultiplier(scale) * 100) + "%"));

        source.sendSystemMessage(Component.literal("Current mob drop scaling: " +
                formatDouble(100 + ScalableStat.DROPS.getMultiplier(scale) * 100) + "%"));

        source.sendSystemMessage(Component.literal("Current mob experience scaling: " +
                formatDouble(MonsterEvents.getMultipliedStat(100, ScalingMobsConfig.MOB_XP_BASE.get(), ScalingMobsConfig.MOB_XP_RATE.get(), ScalingMobsConfig.MOB_XP_MAX.get(), currentDay)) + "%"));

        return Command.SINGLE_SUCCESS;
    }

    private static String formatDouble(double value)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(value);
    }
}
