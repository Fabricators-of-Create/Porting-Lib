package io.github.fabricators_of_create.porting_lib.crafting;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.extensions.IngredientExtensions;

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

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.IngredientAccessor;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CraftingHelper {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	@SuppressWarnings("unused")
	private static final Marker CRAFTHELPER = MarkerManager.getMarker("CRAFTHELPER");
	private static final BiMap<ResourceLocation, IIngredientSerializer<?>> ingredients = HashBiMap.create();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

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

	public static <T extends Ingredient> void write(FriendlyByteBuf buffer, T ingredient) {
		@SuppressWarnings("unchecked") //I wonder if there is a better way generic wise...
		IIngredientSerializer<T> serializer = (IIngredientSerializer<T>) ((IngredientExtensions) ingredient).getSerializer();
		ResourceLocation key = ingredients.inverse().get(serializer);
		if (key == null)
			throw new IllegalArgumentException("Tried to serialize unregistered Ingredient: " + ingredient + " " + serializer);
		if (serializer != VanillaIngredientSerializer.INSTANCE) {
			buffer.writeVarInt(-1); //Marker to know there is a custom ingredient
			buffer.writeResourceLocation(key);
		}
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
				ingredients.add(merge(vanilla));

			if (ingredients.size() == 0)
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");

			if (ingredients.size() == 1)
				return ingredients.get(0);

			return new CompoundIngredient(ingredients);
		}

		if (!json.isJsonObject())
			throw new JsonSyntaxException("Expcted ingredient to be a object or array of objects");

		JsonObject obj = (JsonObject) json;

		String type = GsonHelper.getAsString(obj, "type", "minecraft:item");
		if (type.isEmpty())
			throw new JsonSyntaxException("Ingredient type can not be an empty string");

		IIngredientSerializer<?> serializer = ingredients.get(new ResourceLocation(type));
		if (serializer == null)
			throw new JsonSyntaxException("Unknown ingredient type: " + type);

		return serializer.parse(obj);
	}

	public static ItemStack getItemStack(JsonObject json, boolean readNBT) {
		String itemName = GsonHelper.getAsString(json, "item");

		Item item = Registry.ITEM.get(new ResourceLocation(itemName));

		if (item == null)
			throw new JsonSyntaxException("Unknown item '" + itemName + "'");

		if (readNBT && json.has("nbt")) {
			// Lets hope this works? Needs test
			try {
				JsonElement element = json.get("nbt");
				CompoundTag nbt;
				if (element.isJsonObject())
					nbt = TagParser.parseTag(GSON.toJson(element));
				else
					nbt = TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));

				CompoundTag tmp = new CompoundTag();

				tmp.put("tag", nbt);
				tmp.putString("id", itemName);
				tmp.putInt("Count", GsonHelper.getAsInt(json, "count", 1));

				return ItemStack.of(tmp);
			} catch (CommandSyntaxException e) {
				throw new JsonSyntaxException("Invalid NBT Entry: " + e);
			}
		}

		return new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));
	}

	public static Ingredient merge(Collection<Ingredient> parts) {
		return IngredientAccessor.port_lib$fromValues(parts.stream().flatMap(i -> Arrays.stream(((IngredientAccessor) i).port_lib$getValues())));
	}

	public static boolean areShareTagsEqual(ItemStack first, ItemStack other) {
		CompoundTag shareTagA = first.getTag();
		CompoundTag shareTagB = other.getTag();
		if (shareTagA == null)
			return shareTagB == null;
		else
			return shareTagB != null && shareTagA.equals(shareTagB);
	}

}
