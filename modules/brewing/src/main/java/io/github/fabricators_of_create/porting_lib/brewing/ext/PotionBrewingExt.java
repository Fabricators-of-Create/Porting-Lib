package io.github.fabricators_of_create.porting_lib.brewing.ext;

import io.github.fabricators_of_create.porting_lib.brewing.BrewingRecipeRegistry;
import io.github.fabricators_of_create.porting_lib.brewing.IBrewingRecipe;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public interface PotionBrewingExt {
	/**
	 * Checks if an item stack is a valid input for brewing,
	 * for use in the lower 3 slots where water bottles would normally go.
	 */
	default boolean isInput(ItemStack stack) {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support isInput(ItemStack)");
	}

	/**
	 * Retrieves recipes that use the more general interface.
	 * This does NOT include the container and potion mixes.
	 */
	default List<IBrewingRecipe> getRecipes() {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support getRecipes()");
	}

	@ApiStatus.Internal
	default void setBrewingRegistry(BrewingRecipeRegistry registry) {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support setBrewingRegistry(BrewingRecipeRegistry)");
	}
}
