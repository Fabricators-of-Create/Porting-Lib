package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface ItemCraftedCallback {
  Event<ItemCraftedCallback> EVENT = EventFactory.createArrayBacked(ItemCraftedCallback.class, callbacks -> (player, crafted, craftMatrix) -> {
    for(ItemCraftedCallback event : callbacks)
      event.onCraft(player, crafted, craftMatrix);
  });

  void onCraft(PlayerEntity player, ItemStack crafted, Inventory container);
}
