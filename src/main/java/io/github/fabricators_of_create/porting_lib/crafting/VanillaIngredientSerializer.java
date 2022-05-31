package io.github.fabricators_of_create.porting_lib.crafting;

import java.util.stream.Stream;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public class VanillaIngredientSerializer implements IIngredientSerializer<Ingredient> {
	public static final VanillaIngredientSerializer INSTANCE = new VanillaIngredientSerializer();

	public Ingredient parse(FriendlyByteBuf buffer) {
		return Ingredient.fromNetwork(buffer);
	}

	public Ingredient parse(JsonObject json) {
		return Ingredient.fromValues(Stream.of(Ingredient.valueFromJson(json)));
	}

	public void write(FriendlyByteBuf buffer, Ingredient ingredient) {
		ingredient.toNetwork(buffer);
	}
}
