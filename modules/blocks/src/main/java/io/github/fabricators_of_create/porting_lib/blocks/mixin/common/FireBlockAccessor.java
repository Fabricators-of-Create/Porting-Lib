package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import net.minecraft.world.level.block.FireBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockAccessor {
	@Invoker
	int callGetIgniteOdds(BlockState state);

	@Invoker
	int callGetBurnOdds(BlockState state);
}
