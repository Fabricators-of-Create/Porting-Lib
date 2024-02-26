package io.github.fabricators_of_create.porting_lib.event.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public interface OverlayRenderCallback {
	ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");

	Event<OverlayRenderCallback> EVENT = EventFactory.createArrayBacked(OverlayRenderCallback.class, callbacks -> (guiGraphics, partialTicks, window, type) -> {
		for (OverlayRenderCallback callback : callbacks) {
			if (callback.onOverlayRender(guiGraphics, partialTicks, window, type)) {
				resetTexture();
				return true;
			}
		}
		resetTexture();
		return false;
	});

	private static void resetTexture() { // in case overlays change it, which is very likely.
		RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
	}

	boolean onOverlayRender(GuiGraphics guiGraphics, float partialTicks, Window window, Types type);

	enum Types {
		AIR,
		CROSSHAIRS,
		PLAYER_HEALTH
	}
}
