package io.github.fabricators_of_create.porting_lib.test.client;

import io.github.fabricators_of_create.porting_lib.test.PortingLibTest;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.ChestRenderer;

public class PortingLibTestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(PortingLibTest.STRANGE_CHEST_BLOCK_ENTITY_TYPE, ChestRenderer::new);
	}
}
