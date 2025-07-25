package io.github.fabricators_of_create.porting_lib.data.extensions;

import io.github.fabricators_of_create.porting_lib.data.ConditionalRecipeOutput;
import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import org.jetbrains.annotations.Nullable;

public interface RecipeOutputExtension {
	/**
	 * Generates a recipe with the given conditions.
	 */
	default void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
	}

	/**
	 * Builds a wrapper around this recipe output that adds conditions to all received recipes.
	 */
	default RecipeOutput withConditions(ICondition... conditions) {
		return new ConditionalRecipeOutput((RecipeOutput) this, conditions);
	}
}
