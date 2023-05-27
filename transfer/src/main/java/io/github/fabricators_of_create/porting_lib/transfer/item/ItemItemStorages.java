package io.github.fabricators_of_create.porting_lib.transfer.item;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public class ItemItemStorages {
	public static ItemApiLookup<Storage<ItemVariant>, ContainerItemContext> ITEM =
			ItemApiLookup.get(PortingLib.id("item_storage_in_item"), Storage.asClass(), ContainerItemContext.class);

	private ItemItemStorages() {
		throw new RuntimeException("you just lost the game");
	}

	public static void init() {
	}

	static {
		ItemItemStorages.ITEM.registerFallback((itemStack, context) -> {
//			ItemVariant variant = context.getItemVariant();
//			if (itemStack.getItem() instanceof BlockItem item && item.getBlock() instanceof ShulkerBoxBlock)
//				return new ShulkerBoxStorage(item, context);
//			if (variant.getItem() instanceof BundleItem bundle)
//				return new BundleStorage(bundle, context);
			return null;
		});
	}
}
