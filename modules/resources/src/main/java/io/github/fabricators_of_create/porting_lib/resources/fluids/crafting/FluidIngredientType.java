package io.github.fabricators_of_create.porting_lib.resources.fluids.crafting;

import com.mojang.serialization.MapCodec;

import io.github.fabricators_of_create.porting_lib.resources.crafting.IngredientType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * This represents the "type" of a {@link FluidIngredient}, providing means of serializing
 * and deserializing the ingredient over both JSON and network, using the {@link #codec}
 * and {@link #streamCodec}, respectively.
 * <p>
 * Note that the {@link #streamCodec()} is only used if {@link FluidIngredient#isSimple()} returns {@code false},
 * as otherwise its contents are synchronized directly to the network.
 *
 * @param <T> The type of fluid ingredient
 * @see IngredientType IngredientType, a similar class for custom item ingredients
 */
public record FluidIngredientType<T extends FluidIngredient>(MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
	public FluidIngredientType(MapCodec<T> mapCodec) {
		this(mapCodec, ByteBufCodecs.fromCodecWithRegistries(mapCodec.codec()));
	}
}
