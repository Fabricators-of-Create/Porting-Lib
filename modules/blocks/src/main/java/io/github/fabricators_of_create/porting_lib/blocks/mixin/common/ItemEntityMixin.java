package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomFrictionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
	public ItemEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState getPosAndState(Level instance, BlockPos pos, Operation<BlockState> original, @Share("pos") LocalRef<BlockPos> posRef, @Share("state") LocalRef<BlockState> stateRef) {
		posRef.set(pos);
		BlockState state = original.call(instance, pos);
		stateRef.set(state);
		return state;
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"))
	private float getCustomFriction(Block instance, Operation<Float> original, @Share("pos") LocalRef<BlockPos> posRef, @Share("state") LocalRef<BlockState> stateRef) {
		if (instance instanceof CustomFrictionBlock frictionBlock)
			return frictionBlock.getFriction(stateRef.get(), level(), posRef.get(), this);
		return 0;
	}
}
