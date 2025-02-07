package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

/**
 * FluidAttributes should not be used and will be removed in the future
 */
@Deprecated(forRemoval = true)
public interface FluidExtensions {
	static FluidAttributes createVanillaFluidAttributes(Fluid fluid) {
		if (fluid instanceof EmptyFluid)
			return FluidAttributes.builder(null, null)
					.translationKey("block.minecraft.air")
					.color(0).density(0).temperature(0).luminosity(0).viscosity(0).build(fluid);
		if (fluid instanceof WaterFluid)
			return FluidAttributes.Water.builder(
							new Identifier("block/water_still"),
							new Identifier("block/water_flow"))
					.overlay(new Identifier("block/water_overlay"))
					.translationKey("block.minecraft.water")
					.color(0xFF3F76E4)
					.sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
					.build(fluid);
		if (fluid instanceof LavaFluid)
			return FluidAttributes.builder(
							new Identifier("block/lava_still"),
							new Identifier("block/lava_flow"))
					.translationKey("block.minecraft.lava")
					.luminosity(15).density(3000).viscosity(6000).temperature(1300)
					.sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA)
					.build(fluid);
		FluidAttributes attributes = PortingHooks.getFluidAttributesFromVariant(fluid);
		if (attributes != null)
			return attributes;
		throw new RuntimeException("Mod fluids must override createAttributes.");
	}

	default FluidAttributes createAttributes() {
		return createVanillaFluidAttributes((Fluid) this);
	}

	default FluidAttributes getAttributes() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
