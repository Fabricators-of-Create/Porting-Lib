package io.github.fabricators_of_create.porting_lib.resources.events;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.crafting.RecipeManager;

import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when the {@link RecipeManager} has received and synced the recipes from the server to the client.
 *
 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
 *
 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
 */
public class RecipesUpdatedEvent extends BaseEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks) {
			callback.onRecipesUpdated(event);
		}
	});

	private final RecipeManager recipeManager;

	@ApiStatus.Internal
	public RecipesUpdatedEvent(RecipeManager recipeManager) {
		this.recipeManager = recipeManager;
	}

	/**
	 * {@return the recipe manager}
	 */
	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onRecipesUpdated(this);
	}

	public interface Callback {
		void onRecipesUpdated(RecipesUpdatedEvent event);
	}
}
