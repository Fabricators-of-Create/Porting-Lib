package io.github.fabricators_of_create.porting_lib.fluids.wrapper;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.sound.SoundActions;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public class FluidAttributeFluidType extends FluidType {
	private final FluidVariant variant;
	private final FluidVariantAttributeHandler handler;
	public FluidAttributeFluidType(FluidVariant variant, FluidVariantAttributeHandler handler) {
		super(Properties.create()
				.viscosity(handler.getViscosity(variant, null))
				.temperature(handler.getTemperature(variant))
				.lightLevel(handler.getLuminance(variant))
				.sound(SoundActions.BUCKET_FILL, handler.getFillSound(variant).get())
				.sound(SoundActions.BUCKET_EMPTY, handler.getEmptySound(variant).get())
				.density(handler.isLighterThanAir(variant) ? -1 : 1)
		);
		this.variant = variant;
		this.handler = handler;
	}

	@Override
	public Component getDescription() {
		return handler.getName(variant);
	}

	@Override
	public int getTemperature(FluidStack stack) {
		return handler.getTemperature(stack.getType());
	}

	@Override
	public int getViscosity(FluidStack stack) {
		return handler.getViscosity(stack.getType(), null);
	}

	@Override
	public int getViscosity(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		if (getter instanceof Level level)
			return handler.getViscosity(FluidVariant.of(state.getType()), level);
		return super.getViscosity(state, getter, pos);
	}

	@Override
	public int getLightLevel(FluidStack stack) {
		return handler.getLuminance(stack.getType());
	}
}
