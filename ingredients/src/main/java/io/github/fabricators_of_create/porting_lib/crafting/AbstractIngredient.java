package io.github.fabricators_of_create.porting_lib.crafting;

import java.util.stream.Stream;

import com.google.gson.JsonElement;

import io.github.tropheusj.serialization_hooks.ingredient.BaseCustomIngredient;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Extension of {@link Ingredient} which makes most methods custom ingredients need to implement abstract, and removes the static constructors
 * Mods are encouraged to extend this class for their custom ingredients
 */
public abstract class AbstractIngredient extends BaseCustomIngredient {
	/**
	 * Empty constructor, for the sake of dynamic ingredients
	 */
	protected AbstractIngredient() {
		super(Stream.of());
	}

	/**
	 * Value constructor, for ingredients that have some vanilla representation
	 */
	protected AbstractIngredient(Stream<? extends Ingredient.Value> values) {
		super(values);
	}

	@Override
	public abstract JsonElement toJson();

	/* Hide vanilla ingredient static constructors to reduce errors with constructing custom ingredients */
}
