package io.github.fabricators_of_create.porting_lib.crafting;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public interface IIngredientSerializer<T extends Ingredient> {

	T parse(FriendlyByteBuf buffer);

	T parse(JsonObject json);

	void write(FriendlyByteBuf buffer, T ingredient);
}
