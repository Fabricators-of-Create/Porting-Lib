package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;

import net.minecraft.world.level.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomFrictionBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Leashable.class)
public interface LeashableMixin {

	@WrapOperation(method = "angularFriction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private static BlockState getPosAndState(Level instance, BlockPos pos, Operation<BlockState> original, @Share("pos") LocalRef<BlockPos> posRef, @Share("state") LocalRef<BlockState> stateRef) {
		posRef.set(pos);
		BlockState state = original.call(instance, pos);
		stateRef.set(state);
		return state;
	}

	@WrapOperation(method = "angularFriction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"))
	private static <E extends Entity & Leashable> float getCustomFriction(Block instance, Operation<Float> original, E entity, @Share("pos") LocalRef<BlockPos> posRef, @Share("state") LocalRef<BlockState> stateRef) {
		if (instance instanceof CustomFrictionBlock frictionBlock)
			return frictionBlock.getFriction(stateRef.get(), entity.level(), posRef.get(), entity);
		return 0;
	}
}
