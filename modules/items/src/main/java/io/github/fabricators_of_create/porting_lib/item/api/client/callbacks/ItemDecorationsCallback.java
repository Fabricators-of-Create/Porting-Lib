package io.github.fabricators_of_create.porting_lib.item.api.client.callbacks;

import io.github.fabricators_of_create.porting_lib.item.api.client.IItemDecorator;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;

public interface ItemDecorationsCallback {
	/**
	 * Allows users to register custom {@linkplain IItemDecorator IItemDecorator} to Items.
	 */
	Event<ItemDecorationsCallback> EVENT = EventFactory.createArrayBacked(ItemDecorationsCallback.class, itemDecorationsCallbacks -> decorators -> {
		for (ItemDecorationsCallback callback : itemDecorationsCallbacks)
			callback.registerDecorators(decorators);
	});

	void registerDecorators(Map<Item, List<IItemDecorator>> decorators);
}
