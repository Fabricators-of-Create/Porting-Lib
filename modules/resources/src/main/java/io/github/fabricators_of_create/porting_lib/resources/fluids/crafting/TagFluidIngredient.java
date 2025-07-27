package io.github.fabricators_of_create.porting_lib.resources.fluids.crafting;

import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.resources.crafting.PortingLibIngredients;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

/**
 * Fluid ingredient that matches all fluids within the given tag.
 * <p>
 * Unlike with ingredients, this is an explicit "type" of fluid ingredient,
 * though it may still be written without a type field, see {@link FluidIngredient#MAP_CODEC_NONEMPTY}
 */
public class TagFluidIngredient extends FluidIngredient {
	public static final MapCodec<TagFluidIngredient> CODEC = TagKey.codec(Registries.FLUID)
			.xmap(TagFluidIngredient::new, TagFluidIngredient::tag).fieldOf("tag");

	private final TagKey<Fluid> tag;

	public TagFluidIngredient(TagKey<Fluid> tag) {
		this.tag = tag;
	}

	@Override
	public boolean test(FluidStack fluidStack) {
		return fluidStack.is(tag);
	}

	@Override
	protected Stream<FluidStack> generateStacks() {
		return BuiltInRegistries.FLUID.getTag(tag)
				.stream()
				.flatMap(HolderSet::stream)
				.map(fluid -> new FluidStack(fluid, FluidConstants.BUCKET));
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public FluidIngredientType<?> getType() {
		return PortingLibIngredients.TAG_FLUID_INGREDIENT_TYPE;
	}

	@Override
	public int hashCode() {
		return tag.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		return obj instanceof TagFluidIngredient tag && tag.tag.equals(this.tag);
	}

	public TagKey<Fluid> tag() {
		return tag;
	}
}
