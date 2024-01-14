package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RailState;

@Mixin(RailState.class)
public interface RailStateAccessor {
	@Accessor("pos")
	BlockPos port_lib$getPos();

	@Invoker("canConnectTo")
	boolean port_lib$canConnectTo(RailState railState);

	@Invoker("removeSoftConnections")
	void port_lib$removeSoftConnections();
}
