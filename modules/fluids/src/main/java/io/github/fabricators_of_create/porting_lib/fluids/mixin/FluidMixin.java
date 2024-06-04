package io.github.fabricators_of_create.porting_lib.fluids.mixin;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.PortingLibFluids;
import io.github.fabricators_of_create.porting_lib.fluids.extensions.FluidExtension;
import io.github.fabricators_of_create.porting_lib.fluids.wrapper.FluidAttributeFluidType;
import io.github.fabricators_of_create.porting_lib.fluids.wrapper.MergingFluidAttributeFluidType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.world.level.material.Fluid;

import net.minecraft.world.level.material.Fluids;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Fluid.class)
public class FluidMixin implements FluidExtension {
	private FluidType portingLibFluidType;
	@Override
	public FluidType getFluidType() {
		var fluid = (Fluid) (Object) this;
		if (portingLibFluidType == null) {
			if (fluid == Fluids.EMPTY)
				portingLibFluidType = PortingLibFluids.EMPTY_TYPE;
			if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER)
				portingLibFluidType = PortingLibFluids.WATER_TYPE;//new MergingFluidAttributeFluidType(PortingLibFluids.WATER_TYPE, FluidVariant.of(fluid), FluidVariantAttributes.getHandler(Fluids.WATER));
			if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA)
				portingLibFluidType = PortingLibFluids.LAVA_TYPE;//new MergingFluidAttributeFluidType(PortingLibFluids.LAVA_TYPE, FluidVariant.of(fluid), FluidVariantAttributes.getHandler(Fluids.LAVA));
		}
		return this.portingLibFluidType;
	}
}
