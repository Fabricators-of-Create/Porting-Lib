package io.github.fabricators_of_create.porting_lib.brewing;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.alchemy.PotionBrewing;

import org.jetbrains.annotations.ApiStatus;

public class RegisterBrewingRecipesEvent extends BaseEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks)
			callback.onRegisterBrewingRecipes(event);
	});

	private final PotionBrewing.Builder builder;

	@ApiStatus.Internal
	public RegisterBrewingRecipesEvent(PotionBrewing.Builder builder) {
		this.builder = builder;
	}

	public PotionBrewing.Builder getBuilder() {
		return builder;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onRegisterBrewingRecipes(this);
	}

	public interface Callback {
		void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event);
	}
}
