package io.github.fabricators_of_create.porting_lib.item.client.callbacks;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.item.client.IItemDecorator;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Allows users to register custom {@linkplain IItemDecorator IItemDecorator} to Items.
 *
 * <p>This event is not cancelable, and does not have a result.
 *
 * <p>This event is fired on the mod-specific event bus, only on the {@linkplain EnvType#CLIENT logical client}.</p>
 */
public class RegisterItemDecorationsEvent extends BaseEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks)
			callback.onRegisterItemDecorations(event);
	});

	private final Map<Item, List<IItemDecorator>> decorators;

	@ApiStatus.Internal
	public RegisterItemDecorationsEvent(Map<Item, List<IItemDecorator>> decorators) {
		this.decorators = decorators;
	}

	/**
	 * Register an ItemDecorator to an Item
	 */
	public void register(ItemLike itemLike, IItemDecorator decorator) {
		List<IItemDecorator> itemDecoratorList = decorators.computeIfAbsent(itemLike.asItem(), item -> new ArrayList<>());
		itemDecoratorList.add(decorator);
	}

	@Override
	public RegisterItemDecorationsEvent sendEvent() {
		EVENT.invoker().onRegisterItemDecorations(this);
		return this;
	}

	public interface Callback {
		void onRegisterItemDecorations(RegisterItemDecorationsEvent event);
	}
}
