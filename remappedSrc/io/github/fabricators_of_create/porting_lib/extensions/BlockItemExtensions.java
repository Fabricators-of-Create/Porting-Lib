package io.github.fabricators_of_create.porting_lib.extensions;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public interface BlockItemExtensions {
	default void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
		blockToItemMap.remove(((BlockItem)this).getBlock());
	}
}
