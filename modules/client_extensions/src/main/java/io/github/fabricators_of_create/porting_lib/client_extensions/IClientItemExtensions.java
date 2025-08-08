package io.github.fabricators_of_create.porting_lib.client_extensions;

import net.fabricmc.api.EnvType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * {@linkplain EnvType#CLIENT Client-only} extensions to {@link Item}.
 */
public interface IClientItemExtensions {
	IClientItemExtensions DEFAULT = new IClientItemExtensions() {};

	static IClientItemExtensions of(ItemStack stack) {
		return of(stack.getItem());
	}

	static IClientItemExtensions of(Item item) {
		return ClientExtensionsRegistry.ITEM_EXTENSIONS.getOrDefault(item, DEFAULT);
	}
}
