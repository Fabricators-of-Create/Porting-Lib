package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.transfer.internal.cache.ClientBlockApiCache;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemItemStorages;
import net.fabricmc.api.ClientModInitializer;

public class PortingLibTransfer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientBlockApiCache.init();
		ItemItemStorages.init();
	}
}
