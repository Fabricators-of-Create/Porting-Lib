package io.github.fabricators_of_create.porting_lib.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class ConditionalAdvancement {
	public static Builder builder()
	{
		return new Builder();
	}

	/**
	 * Processes the conditional advancement during loading.
	 * @param json The incoming json from the advancement file.
	 * @return The advancement that passed the conditions, or null if none did.
	 */
	@Nullable
	public static JsonObject processConditional(JsonObject json) {
		JsonArray entries = JsonHelper.getArray(json, "advancements", null);
		if (entries == null) {
			return CraftingHelper.processConditions(json, ResourceConditions.CONDITIONS_KEY) ? json : null;
		}

		int idx = 0;
		for (JsonElement ele : entries) {
			if (!ele.isJsonObject())
				throw new JsonSyntaxException("Invalid advancement entry at index " + idx + " Must be JsonObject");
			if (CraftingHelper.processConditions(JsonHelper.getArray(ele.getAsJsonObject(), ResourceConditions.CONDITIONS_KEY)))
				return JsonHelper.getObject(ele.getAsJsonObject(), "advancement");
			idx++;
		}
		return null;
	}

	public static class Builder {
		private List<ConditionJsonProvider[]> conditions = new ArrayList<>();
		private List<Supplier<JsonElement>> advancements = new ArrayList<>();

		private List<ConditionJsonProvider> currentConditions = new ArrayList<>();
		private boolean locked = false;

		public Builder addCondition(ConditionJsonProvider condition) {
			if (locked)
				throw new IllegalStateException("Attempted to modify finished builder");
			currentConditions.add(condition);
			return this;
		}

		public Builder addAdvancement(Consumer<Consumer<Advancement.Task>> callable) {
			if (locked)
				throw new IllegalStateException("Attempted to modify finished builder");
			callable.accept(this::addAdvancement);
			return this;
		}

		public Builder addAdvancement(Advancement.Task advancement) {
			return addAdvancement(advancement::toJson);
		}

		public Builder addAdvancement(RecipeJsonProvider fromRecipe) {
			return addAdvancement(fromRecipe::toAdvancementJson);
		}

		private Builder addAdvancement(Supplier<JsonElement> jsonSupplier) {
			if (locked)
				throw new IllegalStateException("Attempted to modify finished builder");
			if (currentConditions.isEmpty())
				throw new IllegalStateException("Can not add a advancement with no conditions.");
			conditions.add(currentConditions.toArray(new ConditionJsonProvider[currentConditions.size()]));
			advancements.add(jsonSupplier);
			currentConditions.clear();
			return this;
		}

		public JsonObject write() {
			if (!locked) {
				if (!currentConditions.isEmpty())
					throw new IllegalStateException("Invalid builder state: Orphaned conditions");
				if (advancements.isEmpty())
					throw new IllegalStateException("Invalid builder state: No Advancements");
				locked = true;
			}
			JsonObject json = new JsonObject();
			JsonArray array = new JsonArray();
			json.add("advancements", array);
			for (int x = 0; x < conditions.size(); x++)
			{
				JsonObject holder = new JsonObject();

				JsonArray conds = new JsonArray();
				for (ConditionJsonProvider c : conditions.get(x))
					conds.add(c.toJson());
				holder.add(ResourceConditions.CONDITIONS_KEY, conds);
				holder.add("advancement", advancements.get(x).get());

				array.add(holder);
			}
			return json;
		}
	}
}
