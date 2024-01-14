package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public interface BlockItemExtensions {
	default void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
		blockToItemMap.remove(((BlockItem)this).getBlock());
	}
}
