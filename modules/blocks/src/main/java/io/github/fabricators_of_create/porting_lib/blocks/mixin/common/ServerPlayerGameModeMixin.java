package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.HarvestableBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.PlayerDestroyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
	@Shadow
	protected ServerLevel level;

	@Shadow
	@Final
	protected ServerPlayer player;

	// This code is very cursed but it works:tm:
	@WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
	private boolean onDestroyedBlockCheck(ServerLevel instance, BlockPos blockPos, boolean isMoving, Operation<Boolean> original, @Local(ordinal = 1) BlockState state) {
		if (state.getBlock() instanceof PlayerDestroyBlock)
			return false;
		return original.call(instance, blockPos, isMoving);
	}

	@Inject(method = "destroyBlock", at = @At(value = "RETURN", ordinal = 3))
	private void actuallyRemoveBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) BlockState state) {
		if (state.getBlock() instanceof PlayerDestroyBlock block)
			block.onDestroyedByPlayer(state, this.level, pos, this.player, false, this.level.getFluidState(pos));
	}

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;mineBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"))
	private void actuallyRemoveBlockElectricBoogaloo(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) BlockState state, @Local(ordinal = 0) LocalBooleanRef removedRef, @Local(ordinal = 1) boolean canHarvest) {
		if (state.getBlock() instanceof PlayerDestroyBlock block)
			removedRef.set(block.onDestroyedByPlayer(state, this.level, pos, this.player, canHarvest, this.level.getFluidState(pos)));
	}

	@WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean port_lib$canHarvestBlock(ServerPlayer player, BlockState blockstate, Operation<Boolean> operation, BlockPos pos) {
		if (blockstate.getBlock() instanceof HarvestableBlock harvestableBlock)
			return harvestableBlock.canHarvestBlock(blockstate, this.level, pos, player);
		else
			return operation.call(player, blockstate);
	}
}
