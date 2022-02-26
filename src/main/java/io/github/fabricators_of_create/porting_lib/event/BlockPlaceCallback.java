package io.github.fabricators_of_create.porting_lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;

public interface BlockPlaceCallback {
	Event<BlockPlaceCallback> EVENT = EventFactory.createArrayBacked(BlockPlaceCallback.class, callbacks -> context -> {
		for (BlockPlaceCallback callback : callbacks) {
			InteractionResult result = callback.onBlockPlace(context);
			if (result != InteractionResult.PASS) {
				return result;
			}
		}
		return InteractionResult.PASS;
	});

	InteractionResult onBlockPlace(BlockPlaceContext context);
}
