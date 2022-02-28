package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.crafting.IIngredientSerializer;
import net.minecraft.world.item.crafting.Ingredient;

public interface IngredientExtensions {
	default IIngredientSerializer<? extends Ingredient> getSerializer() {
		return null;
	}

	default boolean isSimple() {
		return true;
	}

	default void invalidate() {
	}
}
