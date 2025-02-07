package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FluidBlock.class)
public interface LiquidBlockAccessor {
	@Accessor("fluid")
	FlowableFluid port_lib$getFluid();
}
