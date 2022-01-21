package net.momostudios.scalingmobs;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.momostudios.scalingmobs.config.ScalingMobsConfig;
import net.momostudios.scalingmobs.init.CommandInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("scalingmobs")
public class ScalingMobs
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public ScalingMobs()
    {
        MinecraftForge.EVENT_BUS.register(this);
        ScalingMobsConfig.setup();
    }

    @SubscribeEvent
    public void onCommandRegister(final RegisterCommandsEvent event)
    {
        CommandInit.registerCommands(event);
    }
}
