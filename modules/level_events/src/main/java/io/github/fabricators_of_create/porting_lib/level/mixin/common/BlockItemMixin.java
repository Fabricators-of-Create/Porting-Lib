package io.github.fabricators_of_create.porting_lib.level.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.level.BlockSnapshot;
import io.github.fabricators_of_create.porting_lib.level.LevelHooks;
import net.minecraft.world.item.BlockItem;

import net.minecraft.world.item.context.BlockPlaceContext;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
	@WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean port_lib$callEntityPlaceEvent(BlockItem instance, BlockPlaceContext context, BlockState placedState, Operation<Boolean> original) {
		BlockSnapshot snapshot = BlockSnapshot.create(context.getLevel().dimension(), context.getLevel(), context.getClickedPos());
		boolean value = original.call(instance, context, placedState);

		if (value && LevelHooks.onBlockPlace(context.getPlayer(), snapshot, context.getNearestLookingDirection())) {
			snapshot.restore();
			return false;
		}

		return value;
	}
}
