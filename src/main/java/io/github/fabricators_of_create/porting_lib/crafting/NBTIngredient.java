package io.github.fabricators_of_create.porting_lib.crafting;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.tropheusj.serialization_hooks.IngredientSerializer;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Ingredient that matches the given stack, performing an exact NBT match.
 */
public class NBTIngredient extends AbstractIngredient {
	private final ItemStack stack;

	protected NBTIngredient(ItemStack stack) {
		super(Stream.of(new Ingredient.ItemValue(stack)));
		this.stack = stack;
	}

	/**
	 * Creates a new ingredient matching the given stack and tag
	 */
	public static NBTIngredient of(ItemStack stack) {
		return new NBTIngredient(stack);
	}

	@Override
	public boolean test(@Nullable ItemStack input) {
		if (input == null)
			return false;
		//Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
		return this.stack.getItem() == input.getItem() && this.stack.getDamageValue() == input.getDamageValue() && CraftingHelper.areShareTagsEqual(this.stack, input);
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", IngredientSerializer.REGISTRY.getKey(Serializer.INSTANCE).toString());
		json.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
		json.addProperty("count", stack.getCount());
		if (stack.hasTag())
			json.addProperty("nbt", stack.getTag().toString());
		return json;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeItem(stack);
	}

	@Override
	public IngredientSerializer getSerializer() {
		return Serializer.INSTANCE;
	}

	public static class Serializer implements IngredientSerializer {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public Ingredient fromPacket(FriendlyByteBuf buffer) {
			return new NBTIngredient(buffer.readItem());
		}

		@Override
		public Ingredient fromJsonObject(JsonObject object) {
			return new NBTIngredient(CraftingHelper.getItemStack(object, true));
		}

		@Nullable
		@Override
		public Ingredient fromJsonArray(JsonArray array) {
			return null;
		}
	}
}
