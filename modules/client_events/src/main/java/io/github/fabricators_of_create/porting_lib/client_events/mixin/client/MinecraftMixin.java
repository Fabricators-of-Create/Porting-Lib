package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.InputEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.minecraft.client.Options;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	@Final
	public Options options;

	@Shadow
	@Final
	public ParticleEngine particleEngine;

	@Shadow
	@Nullable
	public LocalPlayer player;

	@Unique
	private InputEvent.InteractionKeyMappingTriggered port_lib$onClickInput(int button, KeyMapping keyMapping, InteractionHand hand) {
		InputEvent.InteractionKeyMappingTriggered event = new InputEvent.InteractionKeyMappingTriggered(button, keyMapping, hand);
		event.sendEvent();
		return event;
	}

	@Inject(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getDirection()Lnet/minecraft/core/Direction;"), cancellable = true)
	private void port_lib$onClickInputEvent(boolean leftClick, CallbackInfo ci, @Local BlockHitResult blockHitResult, @Local BlockPos blockPos, @Share("event") LocalRef<InputEvent.InteractionKeyMappingTriggered> eventRef) {
		InputEvent.InteractionKeyMappingTriggered inputEvent = port_lib$onClickInput(0, this.options.keyAttack, InteractionHand.MAIN_HAND);
		eventRef.set(inputEvent);
		if (inputEvent.isCanceled()) {
			if (inputEvent.shouldSwingHand()) {
				this.particleEngine.crack(blockPos, blockHitResult.getDirection());
				this.player.swing(InteractionHand.MAIN_HAND);
			}
			ci.cancel();
		}
	}

	@ModifyExpressionValue(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
	private boolean port_lib$checkHandSwing(boolean original, @Share("event") LocalRef<InputEvent.InteractionKeyMappingTriggered> eventRef) {
		return original && eventRef.get().shouldSwingHand();
	}

	@Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), cancellable = true)
	private void port_lib$onAttackClickInputEvent(CallbackInfoReturnable<Boolean> cir, @Share("inputEvent") LocalRef<InputEvent.InteractionKeyMappingTriggered> inputEvent, @Local boolean flag) {
		inputEvent.set(port_lib$onClickInput(0, this.options.keyAttack, InteractionHand.MAIN_HAND));

		if (inputEvent.get().isCanceled()) {
			if (inputEvent.get().shouldSwingHand())
				this.player.swing(InteractionHand.MAIN_HAND);

			cir.setReturnValue(flag);
		}
	}

	@WrapWithCondition(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
	private boolean port_lib$swingHandIfEventPermits(LocalPlayer instance, InteractionHand interactionHand, @Share("inputEvent") LocalRef<InputEvent.InteractionKeyMappingTriggered> inputEvent) {
		return inputEvent.get() == null || inputEvent.get().shouldSwingHand();
	}

	@Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0), cancellable = true)
	private void port_lib$callForgeUseInputEvent(CallbackInfo ci, @Share("inputEvent") LocalRef<InputEvent.InteractionKeyMappingTriggered> inputEvent, @Local InteractionHand hand) {
		inputEvent.set(port_lib$onClickInput(1, this.options.keyUse, hand));

		if (inputEvent.get().isCanceled()) {
			if (inputEvent.get().shouldSwingHand())
				this.player.swing(hand);

			ci.cancel();
		}
	}

	@ModifyExpressionValue(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult;shouldSwing()Z"))
	private boolean port_lib$onlySwingHandIfNeeded(boolean original, @Share("inputEvent") LocalRef<InputEvent.InteractionKeyMappingTriggered> inputEvent) {
		return original && (inputEvent.get() == null || inputEvent.get().shouldSwingHand());
	}

	@Inject(method = "pickBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z", ordinal = 0), cancellable = true)
	private void port_lib$callInteractionPickInput(CallbackInfo ci) {
		if (port_lib$onClickInput(2, this.options.keyPickItem, InteractionHand.MAIN_HAND).isCanceled())
			ci.cancel();
	}
}
