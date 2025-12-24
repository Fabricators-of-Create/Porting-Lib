package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.EntityDestroyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RemoveBlockGoal.class)
public class RemoveBlockGoalMixin {
	@Shadow
	@Final
	private Mob removerMob;

	@ModifyExpressionValue(method = "isValidTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
	private boolean checkEntityCanDestroyBlock(boolean original, LevelReader level, BlockPos pos, @Local ChunkAccess chunk) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof EntityDestroyBlock destroyBlock)
			return destroyBlock.canEntityDestroy(state, level, pos, this.removerMob) && original;
		return original;
	}
}
