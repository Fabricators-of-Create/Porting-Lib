package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.player.UseItemOnBlockEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.phys.BlockHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviour$BlockStateBaseMixin {
	@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
	private void callUseItemOnBlockEvent(ItemStack stack, Level level, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir) {
		var useOnContext = new UseOnContext(level, player, hand, player.getItemInHand(hand).copy(), hitResult);
		var event = new UseItemOnBlockEvent(useOnContext, UseItemOnBlockEvent.UsePhase.BLOCK);

		if (event.post()) {
			cir.setReturnValue(event.getCancellationResult());
		}
	}
}
