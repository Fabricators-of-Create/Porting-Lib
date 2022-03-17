package io.github.fabricators_of_create.porting_lib.transfer.item.item;

import java.util.Iterator;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ShulkerBoxStorage implements Storage<ItemVariant> {

	private final ItemStack shulker;

	public ShulkerBoxStorage(ItemStack shulker) {
		if(shulker.getItem() != Items.SHULKER_BOX)
			throw new IllegalArgumentException("ItemStack must be a shulker box, current: " + shulker);

		this.shulker = shulker;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		return null;
	}
}
