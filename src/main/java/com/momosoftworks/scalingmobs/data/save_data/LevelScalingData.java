package com.momosoftworks.scalingmobs.data.save_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class LevelScalingData extends SavedData
{
    protected double scale;

    public static LevelScalingData get(ServerLevel level)
    {   return level.getDataStorage().computeIfAbsent(nbt -> load(level, nbt), LevelScalingData::new, "scaling_mobs:scale");
    }

    public double scale()
    {   return scale;
    }

    public void setScale(double scale)
    {   this.scale = scale;
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putDouble("Scale", scale);
        return nbt;
    }

    public static LevelScalingData load(ServerLevel level, CompoundTag nbt)
    {
        LevelScalingData data = new LevelScalingData();
        if (nbt.contains("Scale"))
        {   data.scale = nbt.getDouble("Scale");
        }
        else data.scale = level.getDayTime() / 24000L;
        return data;
    }
}
