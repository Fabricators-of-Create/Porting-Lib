package io.github.fabricators_of_create.porting_lib.crafting;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ingredient that matches the given items, performing a partial NBT match. Use {@link NBTIngredient} if you want exact match on NBT
 */
public class PartialNBTIngredient extends AbstractIngredient {
	private final Set<Item> items;
	private final CompoundTag nbt;
	private final NbtPredicate predicate;

	protected PartialNBTIngredient(Set<Item> items, CompoundTag nbt) {
		super(items.stream().map(item -> {
			ItemStack stack = new ItemStack(item);
			// copy NBT to prevent the stack from modifying the original, as capabilities or vanilla item durability will modify the tag
			stack.setTag(nbt.copy());
			return new Ingredient.ItemValue(stack);
		}));
		if (items.isEmpty()) {
			throw new IllegalArgumentException("Cannot create a PartialNBTIngredient with no items");
		}
		this.items = Collections.unmodifiableSet(items);
		this.nbt = nbt;
		this.predicate = new NbtPredicate(nbt);
	}

	/**
	 * Creates a new ingredient matching any item from the list, containing the given NBT
	 */
	public static PartialNBTIngredient of(CompoundTag nbt, ItemLike... items) {
		return new PartialNBTIngredient(Arrays.stream(items).map(ItemLike::asItem).collect(Collectors.toSet()), nbt);
	}

	/**
	 * Creates a new ingredient matching the given item, containing the given NBT
	 */
	public static PartialNBTIngredient of(ItemLike item, CompoundTag nbt) {
		return new PartialNBTIngredient(Set.of(item.asItem()), nbt);
	}

	@Override
	public boolean test(@Nullable ItemStack input) {
		if (input == null)
			return false;
		return items.contains(input.getItem()) && predicate.matches(input.getTag());
	}

	@Override
	public IngredientDeserializer getDeserializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
		if (items.size() == 1) {
			json.addProperty("item", Registry.ITEM.getKey(items.iterator().next()).toString());
		} else {
			JsonArray items = new JsonArray();
			// ensure the order of items in the set is deterministic when saved to JSON
			this.items.stream().map(Registry.ITEM::getKey).sorted().forEach(name -> items.add(name.toString()));
			json.add("items", items);
		}
		json.addProperty("nbt", nbt.toString());
		return json;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(Serializer.ID);
		buffer.writeVarInt(items.size());
		for (Item item : items)
			buffer.writeResourceLocation(Registry.ITEM.getKey(item));
		buffer.writeNbt(nbt);
	}

	public static class Serializer implements IngredientDeserializer {
		public static final ResourceLocation ID = new ResourceLocation("forge", "partial_nbt");
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public Ingredient fromJson(JsonObject json) {
			// parse items
			Set<Item> items;
			if (json.has("item"))
				items = Set.of(CraftingHelper.getItem(GsonHelper.getAsString(json, "item"), true));
			else if (json.has("items")) {
				ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
				JsonArray itemArray = GsonHelper.getAsJsonArray(json, "items");
				for (int i = 0; i < itemArray.size(); i++) {
					builder.add(CraftingHelper.getItem(GsonHelper.convertToString(itemArray.get(i), "items[" + i + ']'), true));
				}
				items = builder.build();
			} else
				throw new JsonSyntaxException("Must set either 'item' or 'items'");

			// parse NBT
			if (!json.has("nbt"))
				throw new JsonSyntaxException("Missing nbt, expected to find a String or JsonObject");
			CompoundTag nbt = CraftingHelper.getNBT(json.get("nbt"));

			return new PartialNBTIngredient(items, nbt);
		}

		@Override
		public Ingredient fromNetwork(FriendlyByteBuf buffer) {
			Set<Item> items = Stream.generate(() -> Registry.ITEM.get(buffer.readResourceLocation())).limit(buffer.readVarInt()).collect(Collectors.toSet());
			CompoundTag nbt = buffer.readNbt();
			return new PartialNBTIngredient(items, Objects.requireNonNull(nbt));
		}
	}
}
