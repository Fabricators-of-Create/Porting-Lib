package io.github.fabricators_of_create.porting_lib.fluids.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlowingFluid.class)
public interface FlowingFluidAccessor {
	@Invoker
	boolean callCanConvertToSource(Level level);
}
