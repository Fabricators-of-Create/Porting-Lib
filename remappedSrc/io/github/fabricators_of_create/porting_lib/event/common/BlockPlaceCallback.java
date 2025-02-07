package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;

@Deprecated(forRemoval = true)
public interface BlockPlaceCallback {
	/**
	 * @deprecated use BlockEvents.BEFORE_PLACE
	 */
	@Deprecated(forRemoval = true)
	Event<BlockPlaceCallback> EVENT = EventFactory.createArrayBacked(BlockPlaceCallback.class, callbacks -> context -> {
		for (BlockPlaceCallback callback : callbacks) {
			ActionResult result = callback.onBlockPlace(context);
			if (result != ActionResult.PASS)
				return result;
		}
		return ActionResult.PASS;
	});

	ActionResult onBlockPlace(ItemPlacementContext context);
}
