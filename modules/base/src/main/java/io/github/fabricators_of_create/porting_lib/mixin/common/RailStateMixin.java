package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.fabricators_of_create.porting_lib.block.SlopeCreationCheckingRailBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(RailState.class)
public abstract class RailStateMixin {

	@Final
	@Shadow
	private BaseRailBlock block;

	@Shadow
	private BlockState state;

	@Shadow
	@Final
	private Level level;

	@WrapOperation(method = { "connectTo", "place" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseRailBlock;isRail(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
	private boolean port_lib$wrapRailChecksToCheckSlopes(Level level, BlockPos blockPos, Operation<Boolean> original) {
		boolean canMakeSlopes = true;
		if (block instanceof SlopeCreationCheckingRailBlock checking) {
			canMakeSlopes = checking.canMakeSlopes(state, this.level, blockPos);
		}
		return original.call(level, blockPos) && canMakeSlopes;
	}

}
