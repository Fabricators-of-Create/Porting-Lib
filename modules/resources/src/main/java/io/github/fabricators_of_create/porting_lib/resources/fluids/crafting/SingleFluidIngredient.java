package io.github.fabricators_of_create.porting_lib.resources.fluids.crafting;

import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.resources.crafting.PortingLibIngredients;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.Holder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * Fluid ingredient that only matches the fluid of the given stack.
 * <p>
 * Unlike with ingredients, this is an explicit "type" of fluid ingredient,
 * though it may still be written without a type field, see {@link FluidIngredient#MAP_CODEC_NONEMPTY}
 */
public class SingleFluidIngredient extends FluidIngredient {
	public static final MapCodec<SingleFluidIngredient> CODEC = FluidStack.FLUID_NON_EMPTY_CODEC
			.xmap(SingleFluidIngredient::new, SingleFluidIngredient::fluid).fieldOf("fluid");

	private final Holder<Fluid> fluid;

	public SingleFluidIngredient(Holder<Fluid> fluid) {
		if (fluid.is(Fluids.EMPTY.builtInRegistryHolder())) {
			throw new IllegalStateException("SingleFluidIngredient must not be constructed with minecraft:empty, use FluidIngredient.empty() instead!");
		}
		this.fluid = fluid;
	}

	@Override
	public boolean test(FluidStack fluidStack) {
		return fluidStack.is(fluid);
	}

	@Override
	protected Stream<FluidStack> generateStacks() {
		return Stream.of(new FluidStack(fluid, FluidConstants.BUCKET));
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public FluidIngredientType<?> getType() {
		return PortingLibIngredients.SINGLE_FLUID_INGREDIENT_TYPE;
	}

	@Override
	public int hashCode() {
		return this.fluid().value().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		return obj instanceof SingleFluidIngredient other && other.fluid.is(this.fluid);
	}

	public Holder<Fluid> fluid() {
		return fluid;
	}
}
