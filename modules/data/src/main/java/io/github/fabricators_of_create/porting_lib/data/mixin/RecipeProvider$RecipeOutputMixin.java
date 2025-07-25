package io.github.fabricators_of_create.porting_lib.data.mixin;

import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import io.github.fabricators_of_create.porting_lib.resources.conditions.PortingLibConditions;
import io.github.fabricators_of_create.porting_lib.resources.conditions.WithConditions;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mixin(targets = "net.minecraft.data.recipes.RecipeProvider$1")
public abstract class RecipeProvider$RecipeOutputMixin implements RecipeOutput {
	@Shadow
	@Final
	Set<ResourceLocation> val$allRecipes;

	@Shadow
	@Final
	List<CompletableFuture<?>> val$tasks;

	@Shadow
	@Final
	CachedOutput val$cache;

	@Shadow
	@Final
	HolderLookup.Provider val$registries;

	@Shadow
	@Final
	RecipeProvider field_46148;

	public void accept(ResourceLocation location, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
		if (!val$allRecipes.add(location)) {
			throw new IllegalStateException("Duplicate recipe " + location);
		} else {
			val$tasks.add(DataProvider.saveStable(val$cache, val$registries, PortingLibConditions.CONDITIONAL_RECIPES_CODEC, Optional.of(new WithConditions<>(recipe, conditions)), ((RecipeProviderAccessor) field_46148).getRecipePathProvider().json(location)));
			if (advancement != null) {
				val$tasks.add(DataProvider.saveStable(val$cache, val$registries, PortingLibConditions.CONDITIONAL_ADVANCEMENTS_CODEC, Optional.of(new WithConditions<>(advancement.value(), conditions)), ((RecipeProviderAccessor) field_46148).getAdvancementPathProvider().json(advancement.id())));
			}

		}
	}
}
