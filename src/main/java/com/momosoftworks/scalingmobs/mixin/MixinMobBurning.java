package com.momosoftworks.scalingmobs.mixin;

import com.momosoftworks.scalingmobs.config.ScalingMobsConfig;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MixinMobBurning
{
    @Inject(method = "isSunBurnTick", at = @At("RETURN"), cancellable = true)
    public void isInDaylight(CallbackInfoReturnable<Boolean> cir)
    {
        Mob mob = (Mob) (Object) this;
        int burnDay = ScalingMobsConfig.STOP_BURNING_DAY.get();
        boolean canBurnOnThisDay = (int) (mob.level().getDayTime() / 24000L) <= burnDay;

        cir.setReturnValue(cir.getReturnValue() && canBurnOnThisDay);
    }
}
