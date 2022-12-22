package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public record SimpleRecipeType<T extends Recipe<?>>(ResourceLocation name) implements RecipeType<T> {
	@Override
	public String toString() {
		return name.toString();
	}
}
