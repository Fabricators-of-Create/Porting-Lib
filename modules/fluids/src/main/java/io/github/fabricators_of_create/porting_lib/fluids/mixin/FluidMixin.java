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
		var handler = FluidVariantAttributes.getHandler(fluid);
		if (portingLibFluidType == null && handler == null) {
			if (fluid == Fluids.EMPTY)
				portingLibFluidType = new MergingFluidAttributeFluidType(PortingLibFluids.EMPTY_TYPE, FluidVariant.of(fluid), handler);
			if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER)
				portingLibFluidType = new MergingFluidAttributeFluidType(PortingLibFluids.WATER_TYPE, FluidVariant.of(fluid), handler);
			if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA)
				return new MergingFluidAttributeFluidType(PortingLibFluids.LAVA_TYPE, FluidVariant.of(fluid), handler);
			portingLibFluidType = new FluidAttributeFluidType(FluidVariant.of(fluid), handler);
		}
		if (portingLibFluidType == null)
			throw new RuntimeException("Mod fluids must override getFluidType.");
		return this.portingLibFluidType;
	}
}
