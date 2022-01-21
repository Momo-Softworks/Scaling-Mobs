package net.momostudios.scalingmobs.mixin;

import net.minecraft.entity.MobEntity;
import net.momostudios.scalingmobs.config.ScalingMobsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MobEntity.class)
public class MixinMobBurning
{
    /**
     * @author iMikul
     * @reason Stop mobs from burning in day
     */
    @Overwrite
    public boolean isInDaylight()
    {
        MobEntity mob = (MobEntity) (Object) this;
        int burnDay = ScalingMobsConfig.getInstance().getMobsStopBurningDay();

        return (int) (mob.world.getDayTime() / 24000L) < burnDay && !mob.world.isRemote() && mob.world.isDaytime() && mob.ticksExisted % 20 == 0;
    }
}
