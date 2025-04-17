package com.momosoftworks.scalingmobs.mixin;

import com.momosoftworks.scalingmobs.api.event.InventoryChangedEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class MixinInventoryChanged
{
    Inventory self = (Inventory) (Object) this;
    Player player = self.player;

    @Inject(method = "setChanged", at = @At("HEAD"))
    private void onInventoryChanged(CallbackInfo ci)
    {
        MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(player, self));
    }
}
