package io.github.fabricators_of_create.porting_lib.crafting;

import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import org.jetbrains.annotations.Nullable;

/**
 * Ingredient that matches the given stack, performing an exact NBT match.
 */
public class NBTIngredient extends AbstractIngredient {
	private final ItemStack stack;

	protected NBTIngredient(ItemStack stack) {
		super(Stream.of(new Ingredient.StackEntry(stack)));
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
		return this.stack.getItem() == input.getItem() && this.stack.getDamage() == input.getDamage() && CraftingHelper.areShareTagsEqual(this.stack, input);
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", IngredientDeserializer.REGISTRY.getId(Serializer.INSTANCE).toString());
		json.addProperty("item", Registry.ITEM.getId(stack.getItem()).toString());
		json.addProperty("count", stack.getCount());
		if (stack.hasNbt())
			json.addProperty("nbt", stack.getNbt().toString());
		return json;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeIdentifier(Serializer.ID);
		buffer.writeItemStack(stack);
	}

	@Override
	public IngredientDeserializer getDeserializer() {
		return Serializer.INSTANCE;
	}

	public static class Serializer implements IngredientDeserializer {
		public static final Identifier ID = new Identifier("forge", "nbt");
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public Ingredient fromNetwork(PacketByteBuf buffer) {
			return new NBTIngredient(buffer.readItemStack());
		}

		@Nullable
		@Override
		public Ingredient fromJson(JsonObject object) {
			return new NBTIngredient(CraftingHelper.getItemStack(object.getAsJsonObject(), true));
		}
	}
}
