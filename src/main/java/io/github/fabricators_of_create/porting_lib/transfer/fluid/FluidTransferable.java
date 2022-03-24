package io.github.fabricators_of_create.porting_lib.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public interface FluidTransferable {
	@Nullable
	Storage<FluidVariant> getFluidStorage(@Nullable Direction face);

	default boolean shouldWorkClientSide() {
		return true;
	}
}
