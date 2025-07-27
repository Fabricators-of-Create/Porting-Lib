package io.github.fabricators_of_create.porting_lib.resources.crafting;

import com.mojang.serialization.MapCodec;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record IngredientType<T extends CustomIngredient>(ResourceLocation id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) implements CustomIngredientSerializer<T> {
	/**
	 * Constructor for ingredient types that use a regular codec for network syncing.
	 */
	public IngredientType(ResourceLocation id, MapCodec<T> codec) {
		this(id, codec, ByteBufCodecs.fromCodecWithRegistries(codec.codec()));
	}

	@Override
	public ResourceLocation getIdentifier() {
		return id;
	}

	@Override
	public MapCodec<T> getCodec(boolean allowEmpty) {
		return codec;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, T> getPacketCodec() {
		return streamCodec;
	}
}
