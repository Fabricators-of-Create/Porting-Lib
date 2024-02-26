package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ItemCraftedCallback {
  Event<ItemCraftedCallback> EVENT = EventFactory.createArrayBacked(ItemCraftedCallback.class, callbacks -> (player, crafted, craftMatrix) -> {
    for(ItemCraftedCallback event : callbacks)
      event.onCraft(player, crafted, craftMatrix);
  });

  void onCraft(Player player, ItemStack crafted, Container container);
}
