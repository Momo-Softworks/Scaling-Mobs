package com.momosoftworks.scalingmobs.mixin;

import com.momosoftworks.scalingmobs.api.event.LivingFindTargetEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.sensing.PlayerSensor;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Mixin(NearestAttackableTargetGoal.class)
public class MixinMobTargeting
{
    private static final Field MOB = ObfuscationReflectionHelper.findField(TargetGoal.class, "f_26135_");

    @Shadow
    protected LivingEntity target;

    private LivingEntity getMob()
    {   try
        {   return (LivingEntity) MOB.get(this);
        }
        catch (IllegalAccessException e)
        {   e.printStackTrace();
            return null;
        }
    }

    @Inject(method = "findTarget", at = @At("TAIL"))
    public void findTarget(CallbackInfo ci)
    {
        if (target != null)
        {   if (MinecraftForge.EVENT_BUS.post(new LivingFindTargetEvent(this.getMob(), this.target)))
            {   this.target = null;
            }
        }
    }

    @Mixin(PlayerSensor.class)
    public static final class Sensor
    {
        @Inject(method = "doTick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
        private void onTick(ServerLevel level, LivingEntity entity, CallbackInfo ci,
                            // locals
                            List<Player> allPlayers, Brain<?> brain, List<Player> targetablePlayers, Optional<Player> target)
        {
            target.ifPresent(player -> MinecraftForge.EVENT_BUS.post(new LivingFindTargetEvent(entity, player)));
        }
    }
}
