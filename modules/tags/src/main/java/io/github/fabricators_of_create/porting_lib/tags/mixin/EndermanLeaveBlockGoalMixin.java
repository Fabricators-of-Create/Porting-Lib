package io.github.fabricators_of_create.porting_lib.tags.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanLeaveBlockGoal")
public class EndermanLeaveBlockGoalMixin {
	@ModifyReturnValue(method = "canPlaceBlock", at = @At("RETURN"))
	private boolean checkCanPlaceTag(boolean original, Level level, BlockPos destinationPos, BlockState carriedState, BlockState destinationState, BlockState belowDestinationState, BlockPos belowDestinationPos) {
		return original && !belowDestinationState.is(Tags.Blocks.ENDERMAN_PLACE_ON_BLACKLIST);
	}
}
