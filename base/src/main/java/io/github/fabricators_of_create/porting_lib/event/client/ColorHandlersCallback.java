package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;

public interface ColorHandlersCallback {

  Event<Item> ITEM = EventFactory.createArrayBacked(Item.class, callbacks -> ((itemColors, blockColors) -> {
    for(Item event : callbacks)
      event.registerItemColors(itemColors, blockColors);
  }));

  Event<Block> BLOCK = EventFactory.createArrayBacked(Block.class, callbacks -> blockColors -> {
    for(Block event : callbacks)
      event.registerBlockColors(blockColors);
  });

  interface Item {
    void registerItemColors(ItemColors itemColors, BlockColors blockColors);
  }
  interface Block {
    void registerBlockColors(BlockColors blockColors);
  }
}
