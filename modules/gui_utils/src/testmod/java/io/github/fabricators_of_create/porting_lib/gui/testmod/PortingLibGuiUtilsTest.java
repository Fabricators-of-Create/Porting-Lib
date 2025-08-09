package io.github.fabricators_of_create.porting_lib.gui.testmod;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.gui.events.RegisterGuiLayersEvent;
import io.github.fabricators_of_create.porting_lib.gui.layered.VanillaGuiLayers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public class PortingLibGuiUtilsTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RegisterGuiLayersEvent.EVENT.register(event -> {
			Minecraft mc = Minecraft.getInstance();

			event.registerAboveAll(PortingLib.id("test_above_all"), ((guiGraphics, deltaTracker) -> {
				guiGraphics.drawCenteredString(mc.font, "test above all", mc.getWindow().getWidth() / 2, mc.getWindow().getHeight() / 2, 0xFFFFFF);
			}));

			event.registerBelowAll(PortingLib.id("test_below_all"), ((guiGraphics, deltaTracker) -> {
				guiGraphics.drawCenteredString(mc.font, "test below all", mc.getWindow().getWidth() / 2, mc.getWindow().getHeight() / 2, 0xFFFFFF);
			}));
		});
	}
}
