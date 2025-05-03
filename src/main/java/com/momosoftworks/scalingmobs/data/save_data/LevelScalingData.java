package com.momosoftworks.scalingmobs.data.save_data;

import com.momosoftworks.scalingmobs.data.config.MilestoneData;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class LevelScalingData extends SavedData
{
    protected double scale = 0;
    protected Set<ResourceLocation> reachedMilestones = new HashSet<>();

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

    /**
     * <b>DO NOT</b> modify this set directly without calling {@link #setDirty()}.
     */
    public Set<ResourceLocation> milestones()
    {   return reachedMilestones;
    }

    public boolean addMilestone(ResourceLocation id)
    {
        if (reachedMilestones.add(id))
        {   this.setDirty();
            return true;
        }
        return false;
    }
    public boolean addMilestone(Holder<MilestoneData> milestone)
    {   return milestone.unwrapKey().map(key -> this.addMilestone(key.location())).orElseThrow(() -> new IllegalStateException("Could not add milestone: " + milestone));
    }

    public boolean removeMilestone(ResourceLocation id)
    {
        if (reachedMilestones.remove(id))
        {   this.setDirty();
            return true;
        }
        return false;
    }
    public boolean removeMilestone(Holder<MilestoneData> milestone)
    {   return milestone.unwrapKey().map(key -> this.removeMilestone(key.location())).orElseThrow(() -> new IllegalStateException("Could not remove milestone: " + milestone));
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putDouble("Scale", scale);
        nbt.put("ReachedMilestones", new ListTag()
        {{
            for (ResourceLocation milestone : milestones())
            {   add(StringTag.valueOf(milestone.toString()));
            }
        }});
        return nbt;
    }

    public static LevelScalingData load(ServerLevel level, CompoundTag nbt)
    {
        LevelScalingData data = new LevelScalingData();
        if (nbt.contains("Scale"))
        {   data.scale = nbt.getDouble("Scale");
        }
        else data.scale = level.getDayTime() / 24000L;
        for (Tag milestone : nbt.getList("ReachedMilestones", 8))
        {
            ResourceLocation id = ResourceLocation.tryParse(milestone.getAsString());
            if (id != null)
            {   data.reachedMilestones.add(id);
            }
        }
        return data;
    }
}
