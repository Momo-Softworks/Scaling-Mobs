package com.momosoftworks.scalingmobs.events;

import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import com.momosoftworks.scalingmobs.data.ScalableStat;
import com.momosoftworks.scalingmobs.data.save_data.LevelScalingData;
import com.momosoftworks.scalingmobs.util.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
            double levelScale = LevelScalingData.get(player.getServer()).scale();
            setScale(player, Math.min(levelScale, newScale));
        }
    }

    @SubscribeEvent
    public static void scalePlayerDamage(LivingDamageEvent event)
    {
        double scaleFavor = ScalingMobsConfig.PLAYER_SCALE_FAVOR.get();
        if (scaleFavor == 0) return;
        if (event.getSource().getEntity() instanceof ServerPlayer player && MonsterEvents.isScalingMob(event.getEntity()))
        {
            double scale = LevelScalingData.get(player.getServer()).scale();
            double scaleDifference = getScaleDifference(player);
            double multiplier = 1 + ScalableStat.HEALTH.getMultiplier(scale);
            multiplier = MathHelper.blendLog(1, multiplier, scaleDifference, 0, scale, scaleFavor);

            event.setAmount((float) (event.getAmount() * multiplier));
        }
    }

    @SubscribeEvent
    public static void scalePlayerResistance(LivingDamageEvent event)
    {
        double scaleFavor = ScalingMobsConfig.PLAYER_SCALE_FAVOR.get();
        if (scaleFavor == 0) return;
        if (event.getEntity() instanceof ServerPlayer player && MonsterEvents.isScalingMob(event.getEntity()))
        {
            double scale = LevelScalingData.get(player.getServer()).scale();
            double scaleDifference = getScaleDifference(player);
            double multiplier = 1 + ScalableStat.DAMAGE.getMultiplier(scale);
            multiplier = MathHelper.blendLog(1, multiplier, scaleDifference, 0, scale, scaleFavor);

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
        return LevelScalingData.get(player.getServer()).scale() - playerScaling;
    }
}
