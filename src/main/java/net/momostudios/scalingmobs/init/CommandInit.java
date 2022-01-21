package net.momostudios.scalingmobs.init;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.momostudios.scalingmobs.command.BaseCommand;
import net.momostudios.scalingmobs.command.impl.ScalingMobsCommand;

import java.util.ArrayList;

public class CommandInit
{
    private static final ArrayList<BaseCommand> commands = new ArrayList();

    public static void registerCommands(final RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        commands.add(new ScalingMobsCommand("scalingmobs", 2, true));

        commands.forEach(command -> {
            if (command.isEnabled() && command.setExecution() != null)
            {
                dispatcher.register(command.getBuilder());
            }
        });
    }
}
