package io.github.fabricators_of_create.porting_lib.resources.crafting;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class PortingLibIngredients {
	public static final IngredientType<DataComponentIngredient> DATA_COMPONENT_INGREDIENT_TYPE = new IngredientType<>(PortingLib.id("components"), DataComponentIngredient.CODEC);

	public static void init() {
		CustomIngredientSerializer.register(DATA_COMPONENT_INGREDIENT_TYPE);
	}
}
