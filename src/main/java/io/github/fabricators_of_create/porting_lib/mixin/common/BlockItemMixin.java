package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;

import net.minecraft.core.BlockPos;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.extensions.BlockItemExtensions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin implements BlockItemExtensions {
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	private void port_lib$beforePlace(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult result = BlockEvents.BEFORE_PLACE.invoker().beforePlace(new BlockPlaceContext(context));
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
	private InteractionResult port_lib$afterPlace(InteractionResult placeResult, UseOnContext context) {
		if (placeResult.consumesAction())
			BlockEvents.AFTER_PLACE.invoker().afterPlace(new BlockPlaceContext(context));
		return placeResult;
	}

	@Inject(method = "place", at = @At(value = "TAIL"))
	private void port_lib$postProcessPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir, @Local BlockPos blockPos, @Local BlockState blockState) {
		BlockEvents.POST_PROCESS_PLACE.invoker().postProcessPlace(context, blockPos, blockState);
	}
}
