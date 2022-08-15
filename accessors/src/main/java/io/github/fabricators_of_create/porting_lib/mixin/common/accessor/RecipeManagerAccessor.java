package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import net.minecraft.world.item.crafting.RecipeType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
	@Accessor("recipes")
	Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> port_lib$getRecipes();

	@Invoker("byType")
	<C extends Container, T extends Recipe<C>> Map<ResourceLocation, Recipe<C>> port_lib$byType(RecipeType<T> recipeType);
}
