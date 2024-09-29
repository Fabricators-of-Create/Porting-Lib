package io.github.fabricators_of_create.porting_lib.recipe_book_categories.mixin;

import com.google.common.collect.ImmutableList;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.recipe_book_categories.RecipeBookRegistry;
import net.minecraft.client.ClientRecipeBook;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {
	@Inject(method = "setupCollections", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;"))
	private void setupModdedAggregateCategories(Iterable<Recipe<?>> iterable, RegistryAccess registryAccess, CallbackInfo ci, @Local(ordinal = 1) Map<RecipeBookCategories, List<RecipeCollection>> aggregateCategories) {
		RecipeBookRegistry.AGGREGATE_CATEGORIES.forEach((recipeBookCategories, list) -> {
			aggregateCategories.put(recipeBookCategories, list.stream().flatMap((recipeBookCategoriesx) ->
				aggregateCategories.getOrDefault(recipeBookCategoriesx, ImmutableList.of()).stream()
			).collect(ImmutableList.toImmutableList()));
		});
	}

	@Inject(method = "getCategory", at = @At(value = "INVOKE", target = "Lcom/mojang/logging/LogUtils;defer(Ljava/util/function/Supplier;)Ljava/lang/Object;", ordinal = 0), cancellable = true)
	private static void getCustomRecipeCategory(RecipeHolder<?> recipe, CallbackInfoReturnable<RecipeBookCategories> cir) {
		RecipeBookCategories categories = RecipeBookRegistry.findCategories((RecipeType) recipe.value().getType(), recipe);
		if (categories != null)
			cir.setReturnValue(categories);
	}
}
