package io.github.fabricators_of_create.porting_lib.crafting;

import java.util.stream.Stream;

import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.IngredientAccessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class VanillaIngredientSerializer implements IIngredientSerializer<Ingredient> {
	public static final VanillaIngredientSerializer INSTANCE = new VanillaIngredientSerializer();

	public Ingredient parse(FriendlyByteBuf buffer) {
		return IngredientAccessor.port_lib$fromValues(Stream.generate(() -> new Ingredient.ItemValue(buffer.readItem())).limit(buffer.readVarInt()));
	}

	public Ingredient parse(JsonObject json) {
		return IngredientAccessor.port_lib$fromValues(Stream.of(IngredientAccessor.port_lib$valueFromJson(json)));
	}

	public void write(FriendlyByteBuf buffer, Ingredient ingredient) {
		ItemStack[] items = ingredient.getItems();
		buffer.writeVarInt(items.length);

		for (ItemStack stack : items)
			buffer.writeItem(stack);
	}
}
