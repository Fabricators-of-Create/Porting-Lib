package io.github.fabricators_of_create.porting_lib.crafting;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.fabricators_of_create.porting_lib.extensions.IngredientExtensions;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class CraftingHelper {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	@SuppressWarnings("unused")
	private static final Marker CRAFTHELPER = MarkerManager.getMarker("CRAFTHELPER");
	private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final BiMap<ResourceLocation, IIngredientSerializer<?>> ingredients = HashBiMap.create();

	public static void init() {
		CraftingHelper.register(new ResourceLocation("forge", "compound"), CompoundIngredient.Serializer.INSTANCE);
		CraftingHelper.register(new ResourceLocation("forge", "nbt"), NBTIngredient.Serializer.INSTANCE);
		CraftingHelper.register(new ResourceLocation("minecraft", "item"), VanillaIngredientSerializer.INSTANCE);
	}

	public static <T extends Ingredient> IIngredientSerializer<T> register(ResourceLocation key, IIngredientSerializer<T> serializer) {
		if (ingredients.containsKey(key))
			throw new IllegalStateException("Duplicate recipe ingredient serializer: " + key);
		if (ingredients.containsValue(serializer))
			throw new IllegalStateException("Duplicate recipe ingredient serializer: " + key + " " + serializer);
		ingredients.put(key, serializer);
		return serializer;
	}

	@Nullable
	public static ResourceLocation getID(IIngredientSerializer<?> serializer) {
		return ingredients.inverse().get(serializer);
	}

	@Nullable
	public static ResourceLocation getID(Ingredient ingredient) {
		return getID(ingredient.getSerializer());
	}

	@Nullable
	public static IIngredientSerializer<?> getSerializer(ResourceLocation id) {
		return ingredients.get(id);
	}

	public static <T extends Ingredient> void write(FriendlyByteBuf buffer, T ingredient) {
		@SuppressWarnings("unchecked") //I wonder if there is a better way generic wise...
		IIngredientSerializer<T> serializer = (IIngredientSerializer<T>) ingredient.getSerializer();
		ResourceLocation key = ingredients.inverse().get(serializer);
		if (key == null)
			throw new IllegalArgumentException("Tried to serialize unregistered Ingredient: " + ingredient + " " + serializer);
		buffer.writeResourceLocation(key);
		serializer.write(buffer, ingredient);
	}

	public static Ingredient getIngredient(ResourceLocation type, FriendlyByteBuf buffer) {
		IIngredientSerializer<?> serializer = ingredients.get(type);
		if (serializer == null)
			throw new IllegalArgumentException("Can not deserialize unknown Ingredient type: " + type);
		return serializer.parse(buffer);
	}

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
				ingredients.add(CraftingHelper.merge(vanilla));

			if (ingredients.size() == 0)
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");

			if (ingredients.size() == 1)
				return ingredients.get(0);

			return new CompoundIngredient(ingredients);
		}

		if (!json.isJsonObject())
			throw new JsonSyntaxException("Expcted ingredient to be a object or array of objects");

		JsonObject obj = (JsonObject)json;

		String type = GsonHelper.getAsString(obj, "type", "minecraft:item");
		if (type.isEmpty())
			throw new JsonSyntaxException("Ingredient type can not be an empty string");

		IIngredientSerializer<?> serializer = ingredients.get(new ResourceLocation(type));
		if (serializer == null)
			throw new JsonSyntaxException("Unknown ingredient type: " + type);

		return serializer.parse(obj);
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
		}
		catch (CommandSyntaxException e) {
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
}
