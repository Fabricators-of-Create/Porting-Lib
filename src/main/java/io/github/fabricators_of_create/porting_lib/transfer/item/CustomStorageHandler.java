package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

@SuppressWarnings("UnstableApiUsage")
public interface CustomStorageHandler {
	Storage<ItemVariant> getStorage();
}
