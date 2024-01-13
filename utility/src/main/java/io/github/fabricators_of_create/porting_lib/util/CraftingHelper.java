package io.github.fabricators_of_create.porting_lib.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

/**
 * @deprecated use of this class should be re-evaluated.
 * @see DefaultCustomIngredients
 * @see FabricIngredient
 * @see CustomIngredient
 * @see CustomIngredientSerializer
 */
@Deprecated
public class CraftingHelper {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	public static Ingredient getIngredient(JsonElement json) {
		if (json == null || json.isJsonNull())
			throw new JsonSyntaxException("Json cannot be null");

		if (json.isJsonArray()) {
			List<Ingredient> ingredients = Lists.newArrayList();
			List<Ingredient> vanilla = Lists.newArrayList();
			json.getAsJsonArray().forEach((ele) -> {
				Ingredient ing = CraftingHelper.getIngredient(ele);

				if (ing.getClass() == Ingredient.class) //Vanilla, Due to how we read it splits each itemstack, so we pull out to re-merge later
					vanilla.add(ing);
				else
					ingredients.add(ing);
			});

			if (!vanilla.isEmpty())
				ingredients.add(Ingredient.fromValues(vanilla.stream().flatMap((i) -> Arrays.stream(i.values))));

			if (ingredients.size() == 0)
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");

			if (ingredients.size() == 1)
				return ingredients.get(0);

			return DefaultCustomIngredients.any(ingredients.toArray(Ingredient[]::new));
		}

		if (!json.isJsonObject())
			throw new JsonSyntaxException("Expcted ingredient to be a object or array of objects");

		return Ingredient.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, error -> {
			throw new JsonSyntaxException("Failed to parse ingredient: " + error);
		});
	}

	public static ItemStack getItemStack(JsonObject json, boolean readNBT) {
		return getItemStack(json, readNBT, false);
	}

	public static Item getItem(String itemName, boolean disallowsAirInRecipe) {
		Item item = tryGetItem(itemName, disallowsAirInRecipe);
		if (item == null) {
			if (!BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemName)))
				throw new JsonSyntaxException("Unknown item '" + itemName + "'");
			if (disallowsAirInRecipe && item == Items.AIR)
				throw new JsonSyntaxException("Invalid item: " + itemName);
		}
		return Objects.requireNonNull(item);
	}

	@Nullable
	public static Item tryGetItem(String itemName, boolean disallowsAirInRecipe) {
		ResourceLocation itemKey = new ResourceLocation(itemName);
		if (!BuiltInRegistries.ITEM.containsKey(itemKey))
			return null;

		Item item = BuiltInRegistries.ITEM.get(itemKey);
		if (disallowsAirInRecipe && item == Items.AIR)
			return null;
		return item;
	}

	public static CompoundTag getNBT(JsonElement element) {
		try {
			if (element.isJsonObject())
				return TagParser.parseTag(GSON.toJson(element));
			else
				return TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
		} catch (CommandSyntaxException e) {
			throw new JsonSyntaxException("Invalid NBT Entry: " + e);
		}
	}

	@Nullable
	public static CompoundTag tryGetNBT(JsonElement element) {
		try {
			if (element.isJsonObject())
				return TagParser.parseTag(GSON.toJson(element));
			else
				return TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
		} catch (CommandSyntaxException e) {
			return null;
		}
	}

	public static ItemStack getItemStack(JsonObject json, boolean readNBT, boolean disallowsAirInRecipe) {
		String itemName = GsonHelper.getAsString(json, "item");
		Item item = getItem(itemName, disallowsAirInRecipe);
		if (readNBT && json.has("nbt")) {
			CompoundTag nbt = getNBT(json.get("nbt"));
			CompoundTag tmp = new CompoundTag();
			if (nbt.contains("ForgeCaps")) { // TODO: should we keep this?
				tmp.put("ForgeCaps", nbt.get("ForgeCaps"));
				nbt.remove("ForgeCaps");
			}

			tmp.put("tag", nbt);
			tmp.putString("id", itemName);
			tmp.putInt("Count", GsonHelper.getAsInt(json, "count", 1));

			return ItemStack.of(tmp);
		}

		return new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));
	}

	@Nullable
	public static ItemStack tryGetItemStack(JsonObject json, boolean readNBT, boolean disallowsAirInRecipe) {
		JsonElement nameElement = json.get("name");
		if (nameElement == null || !nameElement.isJsonPrimitive())
			return null;
		String itemName = nameElement.getAsString();
		Item item = tryGetItem(itemName, disallowsAirInRecipe);
		if (readNBT && json.has("nbt")) {
			CompoundTag nbt = tryGetNBT(json.get("nbt"));
			if (nbt == null)
				return null;
			CompoundTag tmp = new CompoundTag();
			if (nbt.contains("ForgeCaps")) { // TODO: should we keep this?
				tmp.put("ForgeCaps", nbt.get("ForgeCaps"));
				nbt.remove("ForgeCaps");
			}

			tmp.put("tag", nbt);
			tmp.putString("id", itemName);
			tmp.putInt("Count", GsonHelper.getAsInt(json, "count", 1));

			return ItemStack.of(tmp);
		}

		return new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));
	}

	//Merges several vanilla Ingredients together. As a quirk of how the json is structured, we can't tell if its a single Ingredient type or multiple so we split per item and re-merge here.
	//Only public for internal use, so we can access a private field in here.
	public static Ingredient merge(Collection<Ingredient> parts) {
		return Ingredient.fromValues(parts.stream().flatMap(i -> Arrays.stream(i.values)));
	}

	/**
	 * Modeled after ItemStack.areItemStackTagsEqual
	 * Uses Item.getNBTShareTag for comparison instead of NBT and capabilities.
	 * Only used for comparing itemStacks that were transferred from server to client using Item.getNBTShareTag.
	 */
	public static boolean areShareTagsEqual(ItemStack stack, ItemStack other) {
		CompoundTag shareTagA = stack.getTag();
		CompoundTag shareTagB = other.getTag();
		if (shareTagA == null)
			return shareTagB == null;
		else
			return shareTagB != null && shareTagA.equals(shareTagB);
	}

	public static Predicate<JsonObject> getConditionPredicate(JsonObject json) {
		return ResourceConditions.get(new ResourceLocation(GsonHelper.getAsString(json, ResourceConditions.CONDITION_ID_KEY)));
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

	public static boolean processConditions(JsonObject json, String memberName) {
		return !json.has(memberName) || processConditions(GsonHelper.getAsJsonArray(json, memberName));
	}
}
