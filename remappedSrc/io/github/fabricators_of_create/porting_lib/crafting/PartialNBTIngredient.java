package io.github.fabricators_of_create.porting_lib.crafting;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

/**
 * Ingredient that matches the given items, performing a partial NBT match. Use {@link NBTIngredient} if you want exact match on NBT
 */
public class PartialNBTIngredient extends AbstractIngredient {
	private final Set<Item> items;
	private final NbtCompound nbt;
	private final NbtPredicate predicate;

	protected PartialNBTIngredient(Set<Item> items, NbtCompound nbt) {
		super(items.stream().map(item -> {
			ItemStack stack = new ItemStack(item);
			// copy NBT to prevent the stack from modifying the original, as capabilities or vanilla item durability will modify the tag
			stack.setNbt(nbt.copy());
			return new Ingredient.StackEntry(stack);
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
	public static PartialNBTIngredient of(NbtCompound nbt, ItemConvertible... items) {
		return new PartialNBTIngredient(Arrays.stream(items).map(ItemConvertible::asItem).collect(Collectors.toSet()), nbt);
	}

	/**
	 * Creates a new ingredient matching the given item, containing the given NBT
	 */
	public static PartialNBTIngredient of(ItemConvertible item, NbtCompound nbt) {
		return new PartialNBTIngredient(Set.of(item.asItem()), nbt);
	}

	@Override
	public boolean test(@Nullable ItemStack input) {
		if (input == null)
			return false;
		return items.contains(input.getItem()) && predicate.test(input.getNbt());
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
			json.addProperty("item", Registry.ITEM.getId(items.iterator().next()).toString());
		} else {
			JsonArray items = new JsonArray();
			// ensure the order of items in the set is deterministic when saved to JSON
			this.items.stream().map(Registry.ITEM::getId).sorted().forEach(name -> items.add(name.toString()));
			json.add("items", items);
		}
		json.addProperty("nbt", nbt.toString());
		return json;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeIdentifier(Serializer.ID);
		buffer.writeVarInt(items.size());
		for (Item item : items)
			buffer.writeIdentifier(Registry.ITEM.getId(item));
		buffer.writeNbt(nbt);
	}

	public static class Serializer implements IngredientDeserializer {
		public static final Identifier ID = new Identifier("forge", "partial_nbt");
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public Ingredient fromJson(JsonObject json) {
			// parse items
			Set<Item> items;
			if (json.has("item"))
				items = Set.of(CraftingHelper.getItem(JsonHelper.getString(json, "item"), true));
			else if (json.has("items")) {
				ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
				JsonArray itemArray = JsonHelper.getArray(json, "items");
				for (int i = 0; i < itemArray.size(); i++) {
					builder.add(CraftingHelper.getItem(JsonHelper.asString(itemArray.get(i), "items[" + i + ']'), true));
				}
				items = builder.build();
			} else
				throw new JsonSyntaxException("Must set either 'item' or 'items'");

			// parse NBT
			if (!json.has("nbt"))
				throw new JsonSyntaxException("Missing nbt, expected to find a String or JsonObject");
			NbtCompound nbt = CraftingHelper.getNBT(json.get("nbt"));

			return new PartialNBTIngredient(items, nbt);
		}

		@Override
		public Ingredient fromNetwork(PacketByteBuf buffer) {
			Set<Item> items = Stream.generate(() -> Registry.ITEM.get(buffer.readIdentifier())).limit(buffer.readVarInt()).collect(Collectors.toSet());
			NbtCompound nbt = buffer.readNbt();
			return new PartialNBTIngredient(items, Objects.requireNonNull(nbt));
		}
	}
}
