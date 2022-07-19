package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings({"removal", "UnstableApiUsage"})
public class FluidVariantFluidAttributesHandler implements FluidVariantAttributeHandler {
	protected final FluidAttributes attributes;

	public FluidVariantFluidAttributesHandler(FluidAttributes attributes) {
		this.attributes = attributes;
	}

	@Override
	public Component getName(FluidVariant fluidVariant) {
		return attributes.getDisplayName(new FluidStack(fluidVariant, FluidConstants.BUCKET));
	}

	@Override
	public Optional<SoundEvent> getFillSound(FluidVariant variant) {
		return Optional.ofNullable(attributes.getFillSound(new FluidStack(variant, FluidConstants.BUCKET)));
	}

	@Override
	public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
		return Optional.ofNullable(attributes.getEmptySound(new FluidStack(variant, FluidConstants.BUCKET)));
	}

	@Override
	public int getLuminance(FluidVariant variant) {
		return attributes.getLuminosity(new FluidStack(variant, FluidConstants.BUCKET));
	}

	@Override
	public int getTemperature(FluidVariant variant) {
		return attributes.getTemperature(new FluidStack(variant, FluidConstants.BUCKET));
	}

	@Override
	public int getViscosity(FluidVariant variant, @Nullable Level world) {
		return attributes.getViscosity(new FluidStack(variant, FluidConstants.BUCKET));
	}

	@Override
	public boolean isLighterThanAir(FluidVariant variant) {
		return attributes.isGaseous(new FluidStack(variant, FluidConstants.BUCKET));
	}
}
