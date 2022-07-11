package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.material.WaterFluid;

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
							new ResourceLocation("block/water_still"),
							new ResourceLocation("block/water_flow"))
					.overlay(new ResourceLocation("block/water_overlay"))
					.translationKey("block.minecraft.water")
					.color(0xFF3F76E4)
					.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
					.build(fluid);
		if (fluid instanceof LavaFluid)
			return FluidAttributes.builder(
							new ResourceLocation("block/lava_still"),
							new ResourceLocation("block/lava_flow"))
					.translationKey("block.minecraft.lava")
					.luminosity(15).density(3000).viscosity(6000).temperature(1300)
					.sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA)
					.build(fluid);
		throw new RuntimeException("Mod fluids must override createAttributes.");
	}

	default FluidAttributes createAttributes() {
		return createVanillaFluidAttributes((Fluid) this);
	}

	default FluidAttributes getAttributes() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
