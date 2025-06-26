package io.github.fabricators_of_create.porting_lib.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BlockHooks {
	public static boolean doPlayerHarvestCheck(Player player, BlockState state, BlockGetter level, BlockPos pos) {
		return doPlayerHarvestCheck(player, state, level, pos, args -> ((Player) args[0]).hasCorrectToolForDrops((BlockState) args[1]));
	}

	public static boolean doPlayerHarvestCheck(Player player, BlockState state, BlockGetter level, BlockPos pos, Operation<Boolean> original) {
		// Call deprecated hasCorrectToolForDrops overload for a fallback value, in turn the non-deprecated overload calls this method
		boolean vanillaValue = original.call(player, state);
		BlockEvents.HarvestCheck event = new BlockEvents.HarvestCheck(player, state, level, pos, vanillaValue);
		event.sendEvent();
		return event.canHarvest();
	}
}
