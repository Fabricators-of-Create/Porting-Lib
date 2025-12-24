package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.client_events.ClientEventHooks;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.InputEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.InputEvent.InteractionKeyMappingTriggered;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	@Final
	public Options options;

	@Shadow
	@Nullable
	public LocalPlayer player;

	@WrapOperation(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
	private boolean onInteractionAttackTriggered(MultiPlayerGameMode instance, BlockPos posBlock, Direction directionFacing, Operation<Boolean> original) {
		InteractionKeyMappingTriggered event = ClientEventHooks.onClickInput(0, this.options.keyAttack, InteractionHand.MAIN_HAND);
		if (event.isCanceled()) {
			return event.shouldSwingHand();
		}
		return original.call(instance, posBlock, directionFacing) && event.shouldSwingHand();
	}

	@Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;", ordinal = 0), cancellable = true)
	private void onInteractionAttackTriggered(CallbackInfoReturnable<Boolean> cir, @Share("input") LocalRef<InteractionKeyMappingTriggered> eventRef, @Local boolean flag) {
		InteractionKeyMappingTriggered event = ClientEventHooks.onClickInput(0, this.options.keyAttack, InteractionHand.MAIN_HAND);
		eventRef.set(event);
		if (event.isCanceled()) {
			if (event.shouldSwingHand())
				this.player.swing(InteractionHand.MAIN_HAND);
			cir.setReturnValue(flag);
		}
	}

	@WrapWithCondition(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
	private boolean swingHandIfEventPermitsAttack(LocalPlayer instance, InteractionHand interactionHand, @Share("input") LocalRef<InteractionKeyMappingTriggered> inputEvent) {
		return inputEvent.get() == null || inputEvent.get().shouldSwingHand();
	}

	@Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0), cancellable = true)
	private void callUseInputEvent(CallbackInfo ci, @Share("input") LocalRef<InputEvent.InteractionKeyMappingTriggered> eventRef, @Local InteractionHand hand) {
		InteractionKeyMappingTriggered event = ClientEventHooks.onClickInput(1, this.options.keyUse, hand);

		eventRef.set(event);

		if (event.isCanceled()) {
			if (event.shouldSwingHand())
				this.player.swing(hand);

			ci.cancel();
		}
	}

	@WrapWithCondition(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V", ordinal = 0))
	private boolean swingHandIfEventPermitsUse(LocalPlayer instance, InteractionHand interactionHand, @Share("input") LocalRef<InteractionKeyMappingTriggered> inputEvent) {
		return inputEvent.get() == null || inputEvent.get().shouldSwingHand();
	}


	@Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V", ordinal = 1), cancellable = true)
	private void onlySwingHandIfNeeded1(CallbackInfo ci, @Share("eventRef") LocalRef<InteractionKeyMappingTriggered> inputEvent) {
		InteractionKeyMappingTriggered event = inputEvent.get();
		if (event != null) {
			if (!event.shouldSwingHand())
				ci.cancel();
		}
	}

	@Inject(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;hasControlDown()Z", shift = At.Shift.BEFORE), cancellable = true)
	private void callInteractionPickInput(CallbackInfo ci) {
		if (ClientEventHooks.onClickInput(2, this.options.keyPickItem, InteractionHand.MAIN_HAND).isCanceled())
			ci.cancel();
	}
}
