package io.github.fabricators_of_create.porting_lib.entity.testmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class PortingLibEntityTestmodClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(PortingLibEntityTestmod.CUSTOM_SLIME, CustomSlimeRenderer::new);
	}
}
