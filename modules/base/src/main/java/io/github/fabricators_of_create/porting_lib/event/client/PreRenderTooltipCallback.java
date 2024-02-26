package io.github.fabricators_of_create.porting_lib.event.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Fired <b>before</b> the tooltip is rendered.
 * This can be used to modify the positioning and font of the tooltip.
 * <p>
 * If this event is cancelled, then the tooltip will not be rendered and the corresponding
 * {@link RenderTooltipBorderColorCallback} will not be fired.</p>
 */
public interface PreRenderTooltipCallback {
	Event<PreRenderTooltipCallback> EVENT = EventFactory.createArrayBacked(PreRenderTooltipCallback.class, preRenderTooltipCallbacks -> (stack, poseStack, x, y, screenWidth, screenHeight, font, components) -> {
		for (PreRenderTooltipCallback callback : preRenderTooltipCallbacks)
			if (callback.onPreRenderTooltip(stack, poseStack, x, y, screenWidth, screenHeight, font, components))
				return true;
		return false;
	});


	/**
	 * @return true to cancel rendering, false otherwise
	 */
	boolean onPreRenderTooltip(@NotNull ItemStack stack, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, @NotNull Font font, @NotNull List<ClientTooltipComponent> components);
}
