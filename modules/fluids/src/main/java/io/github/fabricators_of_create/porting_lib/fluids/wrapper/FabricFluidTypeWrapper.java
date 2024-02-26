package io.github.fabricators_of_create.porting_lib.fluids.wrapper;

import java.util.Optional;

import net.minecraft.world.level.material.Fluid;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.sound.SoundActions;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

/**
 * If you're just using fluid types then make sure you register this for your fluid
 * using {@link net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes#register(Fluid, FluidVariantAttributeHandler)}
 */
public class FabricFluidTypeWrapper implements FluidVariantAttributeHandler {
	private final FluidType type;

	public FabricFluidTypeWrapper(FluidType type) {
		this.type = type;
	}

	@Override
	public Component getName(FluidVariant variant) {
		return type.getDescription(new FluidStack(variant, FluidConstants.BUCKET));
	}

	@Override
	public Optional<SoundEvent> getFillSound(FluidVariant variant) {
		return Optional.ofNullable(type.getSound(new FluidStack(variant, FluidConstants.BUCKET), SoundActions.BUCKET_FILL));
	}

	@Override
	public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
		return Optional.ofNullable(type.getSound(new FluidStack(variant, FluidConstants.BUCKET), SoundActions.BUCKET_EMPTY));
	}

	@Override
	public int getLuminance(FluidVariant variant) {
		return type.getLightLevel(new FluidStack(variant, FluidConstants.BUCKET));
	}

	@Override
	public int getTemperature(FluidVariant variant) {
		return type.getTemperature(new FluidStack(variant, FluidConstants.BUCKET));
	}

	@Override
	public int getViscosity(FluidVariant variant, @Nullable Level world) {
		return type.getViscosity(variant.getFluid().defaultFluidState(), world, null);
	}

	@Override
	public boolean isLighterThanAir(FluidVariant variant) {
		return type.isLighterThanAir();
	}
}
