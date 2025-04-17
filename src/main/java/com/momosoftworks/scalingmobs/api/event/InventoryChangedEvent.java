package com.momosoftworks.scalingmobs.api.event;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class InventoryChangedEvent extends Event
{
    private final Player player;
    private final Inventory inventory;

    public InventoryChangedEvent(Player player, Inventory inventory)
    {
        this.player = player;
        this.inventory = inventory;
    }

    public Player getPlayer()
    {   return player;
    }

    public Inventory getInventory()
    {   return inventory;
    }
}
