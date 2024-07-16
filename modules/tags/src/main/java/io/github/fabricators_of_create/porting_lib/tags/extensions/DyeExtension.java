package io.github.fabricators_of_create.porting_lib.tags.extensions;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface DyeExtension {
	/**
	 * Gets the tag key representing the set of items which provide this dye color.
	 * @return A {@link net.minecraft.tags.TagKey<Item>} representing the set of items which provide this dye color.
	 */
	default TagKey<Item> getTag() {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support getTag()");
	}

	/**
	 * Gets the tag key representing the set of items which are dyed with this color.
	 * @return A {@link net.minecraft.tags.TagKey<Item>} representing the set of items which are dyed with this color.
	 */
	default TagKey<Item> getDyedTag() {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support getDyedTag()");
	}
}
