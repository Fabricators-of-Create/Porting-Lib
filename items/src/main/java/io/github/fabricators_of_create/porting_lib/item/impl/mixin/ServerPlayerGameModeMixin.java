package io.github.fabricators_of_create.porting_lib.item.impl.mixin;

import io.github.fabricators_of_create.porting_lib.item.api.common.addons.UseFirstBehaviorItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
	@Inject(
			method = "useItemOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
					ordinal = 0,
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	public void port_lib$onItemFirstUse(ServerPlayer serverPlayer, Level level, ItemStack itemStack, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
		if (itemStack.getItem() instanceof UseFirstBehaviorItem first) {
			UseOnContext useoncontext = new UseOnContext(serverPlayer, interactionHand, blockHitResult);
			InteractionResult result = first.onItemUseFirst(itemStack, useoncontext);
			if (result != InteractionResult.PASS) cir.setReturnValue(result);
		}
	}
}
