package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {
	@Invoker("popExperience")
	void port_lib$popExperience(ServerWorld level, BlockPos pos, int amount);
}
