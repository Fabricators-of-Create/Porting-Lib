package io.github.fabricators_of_create.porting_lib.brewing.ext;

import io.github.fabricators_of_create.porting_lib.brewing.IBrewingRecipe;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface PotionBrewingBuilderExt {
	/**
	 * Adds a new simple brewing recipe.
	 *
	 * @param input      the ingredient that goes in the same slot as water bottles would
	 * @param ingredient the ingredient that goes in the same slot as nether wart would
	 * @param output     the item stack that will replace the input once brewing is done
	 */
	default void addRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support addRecipe(Ingredient, Ingredient, ItemStack)");
	}

	/**
	 * Adds a new brewing recipe with custom logic.
	 */
	default void addRecipe(IBrewingRecipe recipe) {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support addRecipe(IBrewingRecipe)");
	}
}
