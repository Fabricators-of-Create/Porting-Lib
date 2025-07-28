package io.github.fabricators_of_create.porting_lib.resources.injections;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.world.item.crafting.Ingredient;

public interface IngredientInjection {
	default Ingredient.Value[] port_lib$getValues() {
		throw PortingLib.createMixinException("IngredientInjection#port_lib$getValues()");
	}

	default boolean port_lib$isCustom() {
		throw PortingLib.createMixinException("IngredientInjection#port_lib$isCustom()");
	}

	/**
	 * Returns {@code true} if this ingredient has an empty stack list.
	 * Unlike {@link #isEmpty()}, this will catch "accidentally empty" ingredients,
	 * for example a tag ingredient that has an empty tag.
	 */
	default boolean port_lib$hasNoItems() {
		throw PortingLib.createMixinException("IngredientInjection#port_lib$hasNoItems()");
	}
}
