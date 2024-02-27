package io.github.fabricators_of_create.porting_lib.event.client;

import com.mojang.blaze3d.platform.Window;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiGraphics;

@Environment(EnvType.CLIENT)
public interface OverlayRenderCallback {
	Event<OverlayRenderCallback> EVENT = EventFactory.createArrayBacked(OverlayRenderCallback.class, callbacks -> (guiGraphics, partialTicks, window, type) -> {
		for (OverlayRenderCallback callback : callbacks) {
			if (callback.onOverlayRender(guiGraphics, partialTicks, window, type)) {
				return true;
			}
		}
		return false;
	});

	boolean onOverlayRender(GuiGraphics guiGraphics, float partialTicks, Window window, Types type);

	enum Types {
		AIR,
		CROSSHAIRS,
		PLAYER_HEALTH
	}
}
