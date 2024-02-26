package io.github.fabricators_of_create.porting_lib.gametest;

import io.github.fabricators_of_create.porting_lib.gametest.quickexport.AreaSelectionRenderer;
import io.github.fabricators_of_create.porting_lib.gametest.quickexport.AreaSelectorTooltipProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class PortingLibGameTestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (PortingLibGameTest.AREA_SELECTOR_ENABLED) {
			ItemTooltipCallback.EVENT.register(AreaSelectorTooltipProvider.INSTANCE);
			WorldRenderEvents.AFTER_ENTITIES.register(AreaSelectionRenderer.INSTANCE);
		}
	}
}
