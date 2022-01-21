package net.momostudios.scalingmobs.command.impl;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.momostudios.scalingmobs.command.BaseCommand;
import net.momostudios.scalingmobs.config.ScalingMobsConfig;

public class ScalingMobsCommand extends BaseCommand
{
    public ScalingMobsCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("scaling")
                        .then(Commands.literal("rate")
                                .then(Commands.literal("get")
                                        .executes(source -> getStatsRate(source.getSource()))
                                )
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE))
                                        .executes(source -> setMobRate(source.getSource(), DoubleArgumentType.getDouble(source, "amount")))
                                )
                        )
                        .then(Commands.literal("base")
                                .then(Commands.literal("get")
                                        .executes(source -> getStatsBase(source.getSource()))
                                )
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE))
                                        .executes(source -> setMobBase(source.getSource(), DoubleArgumentType.getDouble(source, "amount")))
                                )
                        )
                        .then(Commands.literal("max")
                                .then(Commands.literal("get")
                                        .executes(source -> getStatsMax(source.getSource()))
                                )
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE))
                                        .executes(source -> setMaxStats(source.getSource(), DoubleArgumentType.getDouble(source, "amount")))
                                )
                        )
                )
                .then(Commands.literal("piercing")
                        .then(Commands.literal("rate")
                                .then(Commands.literal("get")
                                        .executes(source -> getPierceRate(source.getSource()))
                                )
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE))
                                        .executes(source -> setPierceRate(source.getSource(), DoubleArgumentType.getDouble(source, "amount")))
                                )
                        )
                        .then(Commands.literal("base")
                                .then(Commands.literal("get")
                                        .executes(source -> getPierceBase(source.getSource()))
                                )
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE))
                                        .executes(source -> setPierceBase(source.getSource(), DoubleArgumentType.getDouble(source, "amount")))
                                )
                        )
                        .then(Commands.literal("max")
                                .then(Commands.literal("get")
                                        .executes(source -> getPierceMax(source.getSource()))
                                )
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0, Double.MAX_VALUE))
                                        .executes(source -> setMaxPierce(source.getSource(), DoubleArgumentType.getDouble(source, "amount")))
                                )
                        )
                )
                .then(Commands.literal("mode")
                        .then(Commands.literal("exponential")
                                .executes(source -> setExponential(source.getSource(), true))
                        )
                        .then(Commands.literal("linear")
                                .executes(source -> setExponential(source.getSource(), false))
                        )
                        .then(Commands.literal("get")
                                .executes(source -> getExponential(source.getSource()))
                        )
                )
                .then(Commands.literal("burnDay")
                        .then(Commands.literal("get")
                                .executes(source -> getBurnDay(source.getSource()))
                        )
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                .executes(source -> setBurnDay(source.getSource(), IntegerArgumentType.getInteger(source, "amount")))
                        )
                );
    }


    /**
     * Set Commands
     */
    static int setMobRate(CommandSource source, double rate) throws CommandSyntaxException
    {
        ScalingMobsConfig.getInstance().setMobScaleRate(rate);

        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();
        String dgray = TextFormatting.DARK_GRAY.toString();
        String gray = TextFormatting.GRAY.toString();

        // print changes to sender
        source.asPlayer().sendStatusMessage(new StringTextComponent(
                yellow + "Set the rate of mob stat scaling to " +
                white + "+" + ScalingMobsConfig.getInstance().getMobScaleRate() * 100 + "%" + yellow + " per day"), false);

        // print to all players
        for (PlayerEntity player : source.asPlayer().world.getPlayers())
        {
            if (player != source.asPlayer())
            {
                player.sendStatusMessage(new StringTextComponent(
                        gray + source.asPlayer().getName().getString() + ": " + dgray +
                        "Set the rate of mob stat scaling to " + gray + ScalingMobsConfig.getInstance().getMobScaleRate() * 100 + "%"), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    static int setMobBase(CommandSource source, double base) throws CommandSyntaxException
    {
        ScalingMobsConfig.getInstance().setMobStatsBase(base);

        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();
        String dgray = TextFormatting.DARK_GRAY.toString();
        String gray = TextFormatting.GRAY.toString();

        // print changes to sender
        source.asPlayer().sendStatusMessage(new StringTextComponent(
                yellow + "Set the base stats of all mobs to " +
                white + ScalingMobsConfig.getInstance().getMobStatsBase() * 100 + "%" + yellow + " of their original values"), false);

        // print to all players
        for (PlayerEntity player : source.asPlayer().world.getPlayers())
        {
            if (player != source.asPlayer())
            {
                player.sendStatusMessage(new StringTextComponent(
                        gray + source.asPlayer().getName().getString() + ": " + dgray +
                        "Set the base stats of all mobs to " + gray + ScalingMobsConfig.getInstance().getMobStatsBase() * 100 + "%" + dgray + " of their original values"), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    static int setMaxStats(CommandSource source, double max) throws CommandSyntaxException
    {
        ScalingMobsConfig.getInstance().setMaxScaling(max);

        source.asPlayer().sendStatusMessage(new StringTextComponent(
                TextFormatting.YELLOW + "Set the maximum stats of all mobs to " +
                TextFormatting.WHITE + ScalingMobsConfig.getInstance().getMaxScaling() * 100 + "%"), false);

        return Command.SINGLE_SUCCESS;
    }

    static int setPierceRate(CommandSource source, double rate) throws CommandSyntaxException
    {
        ScalingMobsConfig.getInstance().setPiercingRate(rate);

        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();
        String dgray = TextFormatting.DARK_GRAY.toString();
        String gray = TextFormatting.GRAY.toString();

        // print changes to sender
        source.asPlayer().sendStatusMessage(new StringTextComponent(
                yellow + "Set the rate of mob piercing damage increase to " +
                white + "+" + ScalingMobsConfig.getInstance().getPiercingRate() * 100 + "%" + yellow + " per day"), false);

        // print to all players
        for (PlayerEntity player : source.asPlayer().world.getPlayers())
        {
            if (player != source.asPlayer())
            {
                player.sendStatusMessage(new StringTextComponent(
                        gray + source.asPlayer().getName().getString() + ": " + dgray +
                        "Set the rate of mob piercing damage increase to " + gray + ScalingMobsConfig.getInstance().getPiercingRate() * 100 + "%" + dgray + " per day"), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    static int setPierceBase(CommandSource source, double base) throws CommandSyntaxException
    {
        ScalingMobsConfig.getInstance().setPiercingBase(base);

        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();
        String dgray = TextFormatting.DARK_GRAY.toString();
        String gray = TextFormatting.GRAY.toString();

        // print changes to sender
        source.asPlayer().sendStatusMessage(new StringTextComponent(
                yellow + "Set the base amount of mob piercing damage to " +
                white + ScalingMobsConfig.getInstance().getPiercingBase() * 100 + "%"), false);

        // print to all players
        for (PlayerEntity player : source.asPlayer().world.getPlayers())
        {
            if (player != source.asPlayer())
            {
                player.sendStatusMessage(new StringTextComponent(
                        gray + source.asPlayer().getName().getString() + ": " + dgray +
                        "Set the base amount of mob piercing damage to " + gray + ScalingMobsConfig.getInstance().getPiercingBase() * 100 + "%"), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    static int setMaxPierce(CommandSource source, double max) throws CommandSyntaxException
    {
        ScalingMobsConfig.getInstance().setMaxPiercing(max);

        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();
        String dgray = TextFormatting.DARK_GRAY.toString();
        String gray = TextFormatting.GRAY.toString();

        // print changes to sender
        source.asPlayer().sendStatusMessage(new StringTextComponent(
                yellow + "Set the maximum amount of mob piercing damage to " +
                white + ScalingMobsConfig.getInstance().getMaxPiercing() * 100 + "%"), false);

        // print to all players
        for (PlayerEntity player : source.asPlayer().world.getPlayers())
        {
            if (player != source.asPlayer())
            {
                player.sendStatusMessage(new StringTextComponent(
                        gray + source.asPlayer().getName().getString() + ": " + dgray +
                        "Set the maximum amount of mob piercing damage to " + gray + ScalingMobsConfig.getInstance().getMaxPiercing() * 100 + "%"), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    static int setExponential(CommandSource source, boolean exponential) throws CommandSyntaxException
    {
        ScalingMobsConfig.getInstance().setExponentialStats(exponential);

        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();
        String dgray = TextFormatting.DARK_GRAY.toString();
        String gray = TextFormatting.GRAY.toString();

        // print changes to sender
        source.asPlayer().sendStatusMessage(new StringTextComponent(
                yellow + "Set mob damage scaling mode to " +
                white + (ScalingMobsConfig.getInstance().areStatsExponential() ? "exponential" : "linear")), false);

        // print to all players
        for (PlayerEntity player : source.asPlayer().world.getPlayers())
        {
            if (player != source.asPlayer())
            {
                player.sendStatusMessage(new StringTextComponent(
                        gray + source.asPlayer().getName().getString() + ": " + dgray +
                        "Set mob damage scaling mode to " + gray + (ScalingMobsConfig.getInstance().areStatsExponential() ? "exponential" : "linear")), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    static int setBurnDay(CommandSource source, int day) throws CommandSyntaxException
    {
        ScalingMobsConfig.getInstance().setMobsStopBurningDay(day);

        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();
        String dgray = TextFormatting.DARK_GRAY.toString();
        String gray = TextFormatting.GRAY.toString();

        // print changes to sender
        source.asPlayer().sendStatusMessage(new StringTextComponent(
                yellow + "Mobs will now stop burning after day " +
                white + ScalingMobsConfig.getInstance().getMobsStopBurningDay()), false);

        // print to all players
        for (PlayerEntity player : source.asPlayer().world.getPlayers())
        {
            if (player != source.asPlayer())
            {
                player.sendStatusMessage(new StringTextComponent(
                        gray + source.asPlayer().getName().getString() + ": " + dgray +
                        "Set the day of the month to start burning mobs for " + gray + ScalingMobsConfig.getInstance().getMobsStopBurningDay()), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    
    /**
     * Get Commands
     */
    static int getStatsRate(CommandSource source) throws CommandSyntaxException
    {
        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();

        source.asPlayer().sendStatusMessage(new StringTextComponent(yellow + "The rate of mob stat scaling is currently " +
                white + "+" + ScalingMobsConfig.getInstance().getMobScaleRate() * 100 + "%" + yellow + " per day"), false);

        return Command.SINGLE_SUCCESS;
    }

    static int getStatsBase(CommandSource source) throws CommandSyntaxException
    {
        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();

        source.asPlayer().sendStatusMessage(new StringTextComponent(yellow + "The base stats for all mobs is currently " +
                white + ScalingMobsConfig.getInstance().getMobStatsBase() * 100 + "%" + yellow + " of their original values"), false);

        return Command.SINGLE_SUCCESS;
    }

    static int getStatsMax(CommandSource source) throws CommandSyntaxException
    {
        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();

        source.asPlayer().sendStatusMessage(new StringTextComponent(yellow + "The maximum stats for all mobs is currently " +
                white + ScalingMobsConfig.getInstance().getMaxScaling() * 100 + "%" + yellow + " of their original values"), false);

        return Command.SINGLE_SUCCESS;
    }

    static int getPierceRate(CommandSource source) throws CommandSyntaxException
    {
        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();

        source.asPlayer().sendStatusMessage(new StringTextComponent(yellow + "The rate of mob piercing damage increase is currently " +
                white + "+" + ScalingMobsConfig.getInstance().getPiercingRate() * 100 + "%" + yellow + " per day"), false);

        return Command.SINGLE_SUCCESS;
    }

    static int getPierceBase(CommandSource source) throws CommandSyntaxException
    {
        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();

        source.asPlayer().sendStatusMessage(new StringTextComponent(yellow + "The base amount of mob piercing damage is currently " +
                white + ScalingMobsConfig.getInstance().getPiercingBase() * 100 + "%"), false);

        return Command.SINGLE_SUCCESS;
    }

    static int getPierceMax(CommandSource source) throws CommandSyntaxException
    {
        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();

        source.asPlayer().sendStatusMessage(new StringTextComponent(yellow + "The maximum amount of mob piercing damage is currently " +
                white + ScalingMobsConfig.getInstance().getMaxPiercing() * 100 + "%"), false);

        return Command.SINGLE_SUCCESS;
    }

    static int getExponential(CommandSource source) throws CommandSyntaxException
    {
        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();

        source.asPlayer().sendStatusMessage(new StringTextComponent(yellow + "The mob damage scaling mode is currently " +
                white + (ScalingMobsConfig.getInstance().areStatsExponential() ? "exponential" : "linear")), false);

        return Command.SINGLE_SUCCESS;
    }

    static int getBurnDay(CommandSource source) throws CommandSyntaxException
    {
        String yellow = TextFormatting.YELLOW.toString();
        String white = TextFormatting.WHITE.toString();

        source.asPlayer().sendStatusMessage(new StringTextComponent(yellow + "Mobs will currently start burning on day " +
                white + ScalingMobsConfig.getInstance().getMobsStopBurningDay()), false);

        return Command.SINGLE_SUCCESS;
    }
}
