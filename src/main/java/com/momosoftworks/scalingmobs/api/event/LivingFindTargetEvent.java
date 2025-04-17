package com.momosoftworks.scalingmobs.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class LivingFindTargetEvent extends Event
{
    private final LivingEntity entity;
    private final LivingEntity target;

    public LivingFindTargetEvent(LivingEntity entity, LivingEntity target)
    {
        this.entity = entity;
        this.target = target;
    }

    public LivingEntity getEntity()
    {   return entity;
    }

    public LivingEntity getTarget()
    {   return target;
    }
}
