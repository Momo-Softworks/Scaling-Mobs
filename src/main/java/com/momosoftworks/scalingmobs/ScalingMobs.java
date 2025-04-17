package com.momosoftworks.scalingmobs;

import com.momosoftworks.scalingmobs.data.ModRegistries;
import com.momosoftworks.scalingmobs.data.config.MilestoneData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import com.momosoftworks.scalingmobs.init.CommandInit;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ScalingMobs.MOD_ID)
public class ScalingMobs
{
    public static final String MOD_ID = "scalingmobs";
    public static final Logger LOGGER = LogManager.getLogger("Scaling Mobs");

    public ScalingMobs()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        ScalingMobsConfig.setup();

        bus.addListener((DataPackRegistryEvent.NewRegistry event) ->
        {
            event.dataPackRegistry(ModRegistries.MILESTONE_DATA, MilestoneData.CODEC);
        });
    }

    @SubscribeEvent
    public void onCommandRegister(final RegisterCommandsEvent event)
    {   CommandInit.registerCommands(event);
    }
}
