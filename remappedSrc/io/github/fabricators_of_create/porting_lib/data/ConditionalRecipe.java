package io.github.fabricators_of_create.porting_lib.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ConditionalRecipe {
	public static final RecipeSerializer<Recipe<?>> SERIALZIER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier("forge:conditional"), new Serializer<>());

	public static Builder builder() {
		return new Builder();
	}

	public static class Serializer<T extends Recipe<?>> implements RecipeSerializer<T> {
		@SuppressWarnings("unchecked") // We return a nested one, so we can't know what type it is.
		@Override
		public T read(Identifier recipeId, JsonObject json) {
			JsonArray items = JsonHelper.getArray(json, "recipes");
			int idx = 0;
			for (JsonElement ele : items) {
				if (!ele.isJsonObject())
					throw new JsonSyntaxException("Invalid recipes entry at index " + idx + " Must be JsonObject");
				if (processConditions(JsonHelper.getArray(ele.getAsJsonObject(), ResourceConditions.CONDITIONS_KEY)))
					return (T)RecipeManager.deserialize(recipeId, JsonHelper.getObject(ele.getAsJsonObject(), "recipe"));
				idx++;
			}
			return null;
		}

		public static boolean processConditions(JsonArray conditions) {
			for (int x = 0; x < conditions.size(); x++) {
				if (!conditions.get(x).isJsonObject())
					throw new JsonSyntaxException("Conditions must be an array of JsonObjects");

				JsonObject json = conditions.get(x).getAsJsonObject();
				if (!CraftingHelper.getConditionPredicate(json).test(json))
					return false;
			}
			return true;
		}

		//Should never get here as we return one of the recipes we wrap.
		@Override public T read(Identifier recipeId, PacketByteBuf buffer) { return null; }
		@Override public void write(PacketByteBuf buffer, T recipe) {}
	}

	public static class Builder {
		private List<ConditionJsonProvider[]> conditions = new ArrayList<>();
		private List<RecipeJsonProvider> recipes = new ArrayList<>();
		private Identifier advId;
		private ConditionalAdvancement.Builder adv;

		private List<ConditionJsonProvider> currentConditions = new ArrayList<>();

		public Builder addCondition(ConditionJsonProvider condition) {
			currentConditions.add(condition);
			return this;
		}

		public Builder addRecipe(Consumer<Consumer<RecipeJsonProvider>> callable) {
			callable.accept(this::addRecipe);
			return this;
		}

		public Builder addRecipe(RecipeJsonProvider recipe) {
			if (currentConditions.isEmpty())
				throw new IllegalStateException("Can not add a recipe with no conditions.");
			conditions.add(currentConditions.toArray(new ConditionJsonProvider[currentConditions.size()]));
			recipes.add(recipe);
			currentConditions.clear();
			return this;
		}

		public Builder generateAdvancement() {
			return generateAdvancement(null);
		}

		public Builder generateAdvancement(@Nullable Identifier id) {
			ConditionalAdvancement.Builder builder = ConditionalAdvancement.builder();
			for(int i=0;i<recipes.size();i++) {
				for(ConditionJsonProvider cond : conditions.get(i))
					builder = builder.addCondition(cond);
				builder = builder.addAdvancement(recipes.get(i));
			}
			return setAdvancement(id, builder);
		}

		public Builder setAdvancement(ConditionalAdvancement.Builder advancement) {
			return setAdvancement(null, advancement);
		}

		public Builder setAdvancement(String namespace, String path, ConditionalAdvancement.Builder advancement) {
			return setAdvancement(new Identifier(namespace, path), advancement);
		}

		public Builder setAdvancement(@Nullable Identifier id, ConditionalAdvancement.Builder advancement) {
			if (this.adv != null)
				throw new IllegalStateException("Invalid ConditionalRecipeBuilder, Advancement already set");
			this.advId = id;
			this.adv = advancement;
			return this;
		}

		public void build(Consumer<RecipeJsonProvider> consumer, String namespace, String path) {
			build(consumer, new Identifier(namespace, path));
		}

		public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
			if (!currentConditions.isEmpty())
				throw new IllegalStateException("Invalid ConditionalRecipe builder, Orphaned conditions");
			if (recipes.isEmpty())
				throw new IllegalStateException("Invalid ConditionalRecipe builder, No recipes");

			if (advId == null && adv != null) {
				advId = new Identifier(id.getNamespace(), "recipes/" + id.getPath());
			}

			consumer.accept(new Finished(id, conditions, recipes, advId, adv));
		}
	}

	private static class Finished implements RecipeJsonProvider {
		private final Identifier id;
		private final List<ConditionJsonProvider[]> conditions;
		private final List<RecipeJsonProvider> recipes;
		private final Identifier advId;
		private final ConditionalAdvancement.Builder adv;

		private Finished(Identifier id, List<ConditionJsonProvider[]> conditions, List<RecipeJsonProvider> recipes, @Nullable Identifier advId, @Nullable ConditionalAdvancement.Builder adv) {
			this.id = id;
			this.conditions = conditions;
			this.recipes = recipes;
			this.advId = advId;
			this.adv = adv;
		}

		@Override
		public void serialize(JsonObject json) {
			JsonArray array = new JsonArray();
			json.add("recipes", array);
			for (int x = 0; x < conditions.size(); x++) {
				JsonObject holder = new JsonObject();

				JsonArray conds = new JsonArray();
				for (ConditionJsonProvider c : conditions.get(x))
					conds.add(c.toJson());
				holder.add(ResourceConditions.CONDITIONS_KEY, conds);
				holder.add("recipe", recipes.get(x).toJson());

				array.add(holder);
			}
		}

		@Override
		public Identifier getRecipeId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return SERIALZIER;
		}

		@Override
		public JsonObject toAdvancementJson() {
			return adv == null ? null : adv.write();
		}

		@Override
		public Identifier getAdvancementId() {
			return advId;
		}
	}

	public static void init() {}
}
