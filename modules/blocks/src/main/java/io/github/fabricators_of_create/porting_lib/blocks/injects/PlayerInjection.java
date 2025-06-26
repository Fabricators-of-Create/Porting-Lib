package io.github.fabricators_of_create.porting_lib.blocks.injects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import io.github.fabricators_of_create.porting_lib.blocks.BlockHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface PlayerInjection {
	default boolean port_lib$hasCorrectToolForDrops(BlockState state, Level level, BlockPos pos) {
		return BlockHooks.doPlayerHarvestCheck((Player) this, state, level, pos);
	}

	default boolean port_lib$hasCorrectToolForDrops(BlockState state, Level level, BlockPos pos, Operation<Boolean> original) {
		return BlockHooks.doPlayerHarvestCheck((Player) this, state, level, pos, original);
	}
}
