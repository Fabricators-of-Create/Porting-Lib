package io.github.fabricators_of_create.porting_lib.transfer.item.item;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.Items;

public class ItemItemStorages {
	public static ItemApiLookup<Storage<ItemVariant>, ContainerItemContext> ITEM =
			ItemApiLookup.get(PortingLib.id("item_storage_in_item"), Storage.asClass(), ContainerItemContext.class);

	static {
		ItemItemStorages.ITEM.registerFallback((itemStack, context) -> {
			if(context.getItemVariant().getItem() == Items.SHULKER_BOX)
				return new ShulkerBoxStorage(itemStack);
			return null;
		});
	}
}
