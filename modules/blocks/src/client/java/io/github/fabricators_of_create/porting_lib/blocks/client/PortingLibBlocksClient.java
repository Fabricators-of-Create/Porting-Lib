package io.github.fabricators_of_create.porting_lib.blocks.client;

import io.github.fabricators_of_create.porting_lib.blocks.SidedHelper;
import net.fabricmc.api.ClientModInitializer;

public class PortingLibBlocksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SidedHelper.INSTANCE = new ClientSidedHelper();
	}
}
