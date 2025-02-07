package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.extensions.BlockItemExtensions;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin implements BlockItemExtensions {
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	private void port_lib$beforePlace(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		ActionResult result = BlockEvents.BEFORE_PLACE.invoker().beforePlace(new ItemPlacementContext(context));
		if (result != null)
			cir.setReturnValue(result);
	}

	@ModifyExpressionValue(
			method = "useOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/BlockItem;place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;"
			)
	)
	private ActionResult port_lib$afterPlace(ActionResult placeResult, ItemUsageContext context) {
		if (placeResult.isAccepted())
			BlockEvents.AFTER_PLACE.invoker().afterPlace(new ItemPlacementContext(context));
		return placeResult;
	}

	@Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult;sidedSuccess(Z)Lnet/minecraft/world/InteractionResult;"))
	private void port_lib$postProcessPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir, @Local BlockPos blockPos, @Local BlockState blockState) {
		BlockEvents.POST_PROCESS_PLACE.invoker().postProcessPlace(context, blockPos, blockState);
	}
}
