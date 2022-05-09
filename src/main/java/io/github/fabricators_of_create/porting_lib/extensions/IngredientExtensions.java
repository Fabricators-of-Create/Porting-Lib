package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.crafting.IIngredientSerializer;
import net.minecraft.world.item.crafting.Ingredient;

public interface IngredientExtensions {
	default IIngredientSerializer<? extends Ingredient> getSerializer() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default boolean isSimple() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default boolean isVanilla() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void invalidate() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void markValid() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default boolean checkInvalidation() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
