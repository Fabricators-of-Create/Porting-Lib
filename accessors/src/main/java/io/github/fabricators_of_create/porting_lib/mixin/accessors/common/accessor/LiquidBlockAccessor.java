package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

@Mixin(LiquidBlock.class)
public interface LiquidBlockAccessor {
	@Accessor("fluid")
	FlowingFluid port_lib$getFluid();
}
