package io.github.fabricators_of_create.porting_lib.gametest.client;

import io.github.fabricators_of_create.porting_lib.gametest.PortingLibGameTest;
import io.github.fabricators_of_create.porting_lib.gametest.client.quickexport.AreaSelectionRenderer;
import io.github.fabricators_of_create.porting_lib.gametest.client.quickexport.AreaSelectorTooltipProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

public class PortingLibGameTestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (PortingLibGameTest.AREA_SELECTOR_ENABLED) {
			ItemTooltipCallback.EVENT.register(AreaSelectorTooltipProvider.INSTANCE);
			WorldRenderEvents.START_MAIN.register(AreaSelectionRenderer.INSTANCE);
		}
	}
}
