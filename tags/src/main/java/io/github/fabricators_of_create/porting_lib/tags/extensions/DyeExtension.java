package io.github.fabricators_of_create.porting_lib.tags.extensions;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface DyeExtension {
	default TagKey<Item> getTag() {
		return null;
	}
}
