package io.github.fabricators_of_create.porting_lib.resources.fluids.crafting;

import com.mojang.serialization.MapCodec;

import io.github.fabricators_of_create.porting_lib.core.util.PortingLibExtraCodecs;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.resources.crafting.PortingLibIngredients;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.AnyIngredient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Fluid ingredient that matches if any of the child ingredients match.
 * This type additionally represents the array notation used in
 * {@linkplain FluidIngredient#CODEC} internally.
 *
 * @see AnyIngredient CompoundIngredient, its item equivalent
 */
public final class CompoundFluidIngredient extends FluidIngredient {
	public static final MapCodec<CompoundFluidIngredient> CODEC = PortingLibExtraCodecs.aliasedFieldOf(FluidIngredient.LIST_CODEC_NON_EMPTY, "children", "ingredients").xmap(CompoundFluidIngredient::new, CompoundFluidIngredient::children);

	private final List<FluidIngredient> children;

	public CompoundFluidIngredient(List<? extends FluidIngredient> children) {
		if (children.isEmpty()) {
			throw new IllegalArgumentException("Compound fluid ingredient must have at least one child");
		}
		this.children = List.copyOf(children);
	}

	/**
	 * Creates a compound ingredient from the given list of ingredients.
	 */
	public static FluidIngredient of(FluidIngredient... children) {
		if (children.length == 0)
			return FluidIngredient.empty();
		if (children.length == 1)
			return children[0];

		return new CompoundFluidIngredient(List.of(children));
	}

	/**
	 * Creates a compound ingredient from the given list of ingredients.
	 */
	public static FluidIngredient of(List<FluidIngredient> children) {
		if (children.isEmpty())
			return FluidIngredient.empty();
		if (children.size() == 1)
			return children.getFirst();

		return new CompoundFluidIngredient(children);
	}

	public static FluidIngredient of(Stream<FluidIngredient> stream) {
		return of(stream.toList());
	}

	@Override
	public Stream<FluidStack> generateStacks() {
		return children.stream().flatMap(FluidIngredient::generateStacks);
	}

	@Override
	public boolean test(FluidStack stack) {
		for (var child : children) {
			if (child.test(stack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSimple() {
		for (var child : children) {
			if (!child.isSimple()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public FluidIngredientType<?> getType() {
		return PortingLibIngredients.COMPOUND_FLUID_INGREDIENT_TYPE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(children);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		return obj instanceof CompoundFluidIngredient other && other.children.equals(this.children);
	}

	public List<FluidIngredient> children() {
		return children;
	}
}
