package com.momosoftworks.scalingmobs.events;

import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import com.momosoftworks.scalingmobs.data.save_data.LevelScalingData;
import com.momosoftworks.scalingmobs.util.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerScaling
{
    @SubscribeEvent
    public static void increasePlayerScaling(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player)
        {
            double scale = getScale(player);
            boolean exponential = ScalingMobsConfig.EXPONENTIAL_SCALING.get();
            double rate = LevelProgressManager.getScalingSpeed();
            double newScale = exponential ? Math.pow(Math.pow(scale, 1/2d) + rate, 2d)
                                          : scale + rate;
            double levelScale = LevelScalingData.get(player.serverLevel()).scale();
            setScale(player, Math.min(levelScale, newScale));
        }
    }

    @SubscribeEvent
    public static void scalePlayerDamage(LivingDamageEvent event)
    {
        if (event.getSource().getEntity() instanceof ServerPlayer player && MonsterEvents.isScalingMob(event.getEntity()))
        {
            double scale = LevelScalingData.get(player.serverLevel()).scale();
            double scaleDifference = getScaleDifference(player);
            double healthBase = ScalingMobsConfig.MOB_HEALTH_BASE.get();
            double healthRate = ScalingMobsConfig.MOB_HEALTH_RATE.get();
            double healthMax = ScalingMobsConfig.MOB_HEALTH_MAX.get();
            double multiplier = 1+Math.min(healthBase + healthRate * scale, healthMax);
            multiplier = MathHelper.blend(1, multiplier, scaleDifference, 0, scale);

            event.setAmount((float) (event.getAmount() * multiplier));
        }
    }

    @SubscribeEvent
    public static void scalePlayerResistance(LivingDamageEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player && MonsterEvents.isScalingMob(event.getEntity()))
        {
            double scale = LevelScalingData.get(player.serverLevel()).scale();
            double scaleDifference = getScaleDifference(player);
            double damageBase = ScalingMobsConfig.MOB_DAMAGE_BASE.get();
            double damageRate = ScalingMobsConfig.MOB_DAMAGE_RATE.get();
            double damageMax = ScalingMobsConfig.MOB_DAMAGE_MAX.get();
            double multiplier = Math.min(damageBase + damageRate * scale, damageMax);
            multiplier *= MathHelper.blend(1, 1.5, scaleDifference, 0, scale);

            event.setAmount((float) (event.getAmount() / multiplier));
        }
    }

    public static double getScale(Player player)
    {
        CompoundTag playerData = player.getPersistentData();
        return playerData.getDouble("PlayerScaling");
    }

    public static void setScale(Player player, double scaling)
    {
        CompoundTag playerData = player.getPersistentData();
        playerData.putDouble("PlayerScaling", scaling);
    }

    public static double getScaleDifference(ServerPlayer player)
    {
        double playerScaling = getScale(player);
        return LevelScalingData.get(player.serverLevel()).scale() - playerScaling;
    }
}
