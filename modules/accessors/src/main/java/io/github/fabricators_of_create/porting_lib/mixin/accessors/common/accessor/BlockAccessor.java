package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {
	@Invoker("popExperience")
	void port_lib$popExperience(ServerLevel level, BlockPos pos, int amount);
}
