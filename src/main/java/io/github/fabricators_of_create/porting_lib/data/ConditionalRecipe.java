package io.github.fabricators_of_create.porting_lib.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ConditionalRecipe {

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private List<ConditionJsonProvider[]> conditions = new ArrayList<>();
		private List<FinishedRecipe> recipes = new ArrayList<>();

		private List<ConditionJsonProvider> currentConditions = new ArrayList<>();

		public Builder addCondition(ConditionJsonProvider condition) {
			currentConditions.add(condition);
			return this;
		}

		public Builder addRecipe(Consumer<Consumer<FinishedRecipe>> callable) {
			callable.accept(this::addRecipe);
			return this;
		}

		public Builder addRecipe(FinishedRecipe recipe) {
			if (currentConditions.isEmpty())
				throw new IllegalStateException("Can not add a recipe with no conditions.");
			conditions.add(currentConditions.toArray(new ConditionJsonProvider[currentConditions.size()]));
			recipes.add(recipe);
			currentConditions.clear();
			return this;
		}

		public void build(Consumer<FinishedRecipe> consumer, String namespace, String path) {
			build(consumer, new ResourceLocation(namespace, path));
		}

		public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
			if (!currentConditions.isEmpty())
				throw new IllegalStateException("Invalid ConditionalRecipe builder, Orphaned conditions");
			if (recipes.isEmpty())
				throw new IllegalStateException("Invalid ConditionalRecipe builder, No recipes");

			consumer.accept(new Finished(id, conditions, recipes));
		}
	}

	private static class Finished implements FinishedRecipe {
		private final ResourceLocation id;
		private final List<ConditionJsonProvider[]> conditions;
		private final List<FinishedRecipe> recipes;

		private Finished(ResourceLocation id, List<ConditionJsonProvider[]> conditions, List<FinishedRecipe> recipes) {
			this.id = id;
			this.conditions = conditions;
			this.recipes = recipes;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			JsonArray array = new JsonArray();
			json.add("recipes", array);
			for (int x = 0; x < conditions.size(); x++) {
				JsonObject holder = new JsonObject();

				JsonArray conds = new JsonArray();
				for (ConditionJsonProvider c : conditions.get(x))
					conds.add(c.toJson());
				holder.add(ResourceConditions.CONDITIONS_KEY, conds);
				holder.add("recipe", recipes.get(x).serializeRecipe());

				array.add(holder);
			}
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
