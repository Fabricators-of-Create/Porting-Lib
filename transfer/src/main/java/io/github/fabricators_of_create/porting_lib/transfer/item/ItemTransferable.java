package io.github.fabricators_of_create.porting_lib.transfer.item;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;

/**
 * Use {@link net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity}
 */
@Deprecated(forRemoval = true)
public interface ItemTransferable {
	@Nullable
	Storage<ItemVariant> getItemStorage(@Nullable Direction face);

	default boolean canTransferItemsClientSide() {
		return true;
	}
}
