package io.github.fabricators_of_create.porting_lib.recipe_book_categories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.world.item.crafting.RecipeHolder;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Manager for {@link RecipeBookType recipe book types} and {@link RecipeBookCategories categories}.
 * <p>
 * Provides a recipe category lookup.
 */
public class RecipeBookRegistry {

	// Not using ConcurrentHashMap here because it's slower for lookups, so we only use it during init
	public static final Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES = new HashMap<>();
	private static final Map<RecipeBookType, List<RecipeBookCategories>> TYPE_CATEGORIES = new HashMap<>();
	private static final Map<RecipeType<?>, Function<RecipeHolder<?>, RecipeBookCategories>> RECIPE_CATEGORY_LOOKUPS = new HashMap<>();

	/**
	 * Finds the category the specified recipe should display in, or null if none.
	 */
	@Nullable
	public static <T extends Recipe<?>> RecipeBookCategories findCategories(RecipeType<T> type, RecipeHolder<T> recipe) {
		var lookup = RECIPE_CATEGORY_LOOKUPS.get(type);
		return lookup != null ? lookup.apply(recipe) : null;
	}

	@Nullable
	public static List<RecipeBookCategories> getCustomCategoriesOrNull(RecipeBookType recipeBookType) {
		return TYPE_CATEGORIES.getOrDefault(recipeBookType, null);
	}

	/**
	 * Registers the list of categories that compose an aggregate category.
	 */
	public static void registerAggregateCategory(RecipeBookCategories category, List<RecipeBookCategories> others) {
		AGGREGATE_CATEGORIES.put(category, ImmutableList.copyOf(others));
	}

	/**
	 * Registers the list of categories that compose a recipe book.
	 */
	public static void registerBookCategories(RecipeBookType type, List<RecipeBookCategories> categories) {
		TYPE_CATEGORIES.put(type, ImmutableList.copyOf(categories));
	}

	/**
	 * Registers a category lookup for a certain recipe type.
	 */
	public static void registerRecipeCategoryFinder(RecipeType<?> type, Function<RecipeHolder<?>, RecipeBookCategories> lookup) {
		RECIPE_CATEGORY_LOOKUPS.put(type, lookup);
	}
}
