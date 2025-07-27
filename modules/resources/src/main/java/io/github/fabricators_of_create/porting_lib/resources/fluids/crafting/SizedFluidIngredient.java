package io.github.fabricators_of_create.porting_lib.resources.fluids.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.stream.Stream;

import io.github.fabricators_of_create.porting_lib.core.util.PortingLibExtraCodecs;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.resources.crafting.SizedIngredient;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

/**
 * Standard implementation for a FluidIngredient with an amount.
 *
 * <p>{@link FluidIngredient}, like its item counterpart, explicitly does not perform count checks,
 * so this class is used to (a) wrap a standard FluidIngredient with an amount and (b) provide a
 * standard serialization format for mods to use.
 *
 * @see SizedIngredient
 */
public final class SizedFluidIngredient {
	/**
	 * The "flat" codec for {@link SizedFluidIngredient}.
	 *
	 * <p>The amount is serialized inline with the rest of the ingredient, for example:
	 *
	 * <pre>{@code
	 * {
	 *     "fluid": "minecraft:water",
	 *     "amount": 20250
	 * }
	 * }</pre>
	 *
	 * <p>
	 * <p>
	 * Compound fluid ingredients are always serialized using the map codec, i.e.
	 *
	 * <pre>{@code
	 * {
	 *     "type": "porting_lib:compound",
	 *     "ingredients": [
	 *         { "fluid": "minecraft:water" },
	 *         { "fluid": "minecraft:milk" }
	 *     ],
	 *     "amount": 40500
	 * }
	 * }</pre>
	 *
	 * <p>
	 */
	public static final Codec<SizedFluidIngredient> FLAT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
					FluidIngredient.MAP_CODEC_NONEMPTY.forGetter(SizedFluidIngredient::ingredient),
					PortingLibExtraCodecs.optionalFieldAlwaysWrite(PortingLibExtraCodecs.POSITIVE_LONG, "amount", FluidConstants.BUCKET).forGetter(SizedFluidIngredient::amount))
			.apply(instance, SizedFluidIngredient::new));

	/**
	 * The "nested" codec for {@link SizedFluidIngredient}.
	 *
	 * <p>With this codec, the amount is <i>always</i> serialized separately from the ingredient itself, for example:
	 *
	 * <pre>{@code
	 * {
	 *     "ingredient": {
	 *         "fluid": "minecraft:lava"
	 *     },
	 *     "amount": 81000
	 * }
	 * }</pre>
	 */
	public static final Codec<SizedFluidIngredient> NESTED_CODEC = RecordCodecBuilder.create(instance -> instance.group(
					FluidIngredient.CODEC_NON_EMPTY.fieldOf("ingredient").forGetter(SizedFluidIngredient::ingredient),
					PortingLibExtraCodecs.optionalFieldAlwaysWrite(PortingLibExtraCodecs.POSITIVE_LONG, "amount", FluidConstants.BUCKET).forGetter(SizedFluidIngredient::amount))
			.apply(instance, SizedFluidIngredient::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SizedFluidIngredient> STREAM_CODEC = StreamCodec.composite(
			FluidIngredient.STREAM_CODEC,
			SizedFluidIngredient::ingredient,
			ByteBufCodecs.VAR_LONG,
			SizedFluidIngredient::amount,
			SizedFluidIngredient::new);

	public static SizedFluidIngredient of(Fluid fluid, long amount) {
		return new SizedFluidIngredient(FluidIngredient.of(fluid), amount);
	}

	/**
	 * Helper method to create a simple sized ingredient that matches the given fluid stack
	 */
	public static SizedFluidIngredient of(FluidStack stack) {
		return new SizedFluidIngredient(FluidIngredient.single(stack), stack.getAmount());
	}

	/**
	 * Helper method to create a simple sized ingredient that matches fluids in a tag.
	 */
	public static SizedFluidIngredient of(TagKey<Fluid> tag, long amount) {
		return new SizedFluidIngredient(FluidIngredient.tag(tag), amount);
	}

	private final FluidIngredient ingredient;
	private final long amount;

	@Nullable
	private FluidStack[] cachedStacks;

	public SizedFluidIngredient(FluidIngredient ingredient, long amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Size must be positive");
		}
		this.ingredient = ingredient;
		this.amount = amount;
	}

	public FluidIngredient ingredient() {
		return ingredient;
	}

	public long amount() {
		return amount;
	}

	/**
	 * Performs a size-sensitive test on the given stack.
	 *
	 * @return {@code true} if the stack matches the ingredient and has at least the required amount.
	 */
	public boolean test(FluidStack stack) {
		return ingredient.test(stack) && stack.getAmount() >= amount;
	}

	/**
	 * Returns a list of the stacks from this {@link #ingredient}, with an updated {@link #amount}.
	 *
	 * @implNote the array is cached and should not be modified, just like {@link FluidIngredient#getStacks()}}.
	 */
	public FluidStack[] getFluids() {
		if (cachedStacks == null) {
			cachedStacks = Stream.of(ingredient.getStacks())
					.map(s -> s.copyWithAmount(amount))
					.toArray(FluidStack[]::new);
		}
		return cachedStacks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SizedFluidIngredient other)) return false;
		return amount == other.amount && ingredient.equals(other.ingredient);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ingredient, amount);
	}

	@Override
	public String toString() {
		return amount + "x " + ingredient;
	}
}
