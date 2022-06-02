package io.github.fabricators_of_create.porting_lib.crafting;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.tropheusj.serialization_hooks.IngredientSerializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static net.fabricmc.fabric.api.datagen.v1.provider.FabricLootTableProvider.GSON;

public class CraftingHelper {
	public static void init() {
		Registry.register(
				IngredientSerializer.REGISTRY,
				new ResourceLocation("forge", "compound"),
				CompoundIngredient.Serializer.INSTANCE
		);
		Registry.register(
				IngredientSerializer.REGISTRY,
				new ResourceLocation("forge", "nbt"),
				NBTIngredient.Serializer.INSTANCE
		);
	}

	public static ItemStack getItemStack(JsonObject json, boolean readNBT) {
		return getItemStack(json, readNBT, false);
	}

	public static Item getItem(String itemName, boolean disallowsAirInRecipe) {
		ResourceLocation itemKey = new ResourceLocation(itemName);
		if (!Registry.ITEM.containsKey(itemKey))
			throw new JsonSyntaxException("Unknown item '" + itemName + "'");

		Item item = Registry.ITEM.get(itemKey);
		if (disallowsAirInRecipe && item == Items.AIR)
			throw new JsonSyntaxException("Invalid item: " + itemName);
		return Objects.requireNonNull(item);
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
}
