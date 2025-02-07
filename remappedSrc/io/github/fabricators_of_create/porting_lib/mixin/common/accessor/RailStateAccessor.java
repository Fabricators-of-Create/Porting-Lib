package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.block.RailPlacementHelper;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RailPlacementHelper.class)
public interface RailStateAccessor {
	@Accessor("pos")
	BlockPos port_lib$getPos();

	@Invoker("canConnectTo")
	boolean port_lib$canConnectTo(RailPlacementHelper railState);

	@Invoker("removeSoftConnections")
	void port_lib$removeSoftConnections();
}
