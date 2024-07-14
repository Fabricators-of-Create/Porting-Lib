package io.github.fabricators_of_create.porting_lib.entity.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerInteractEvent;
import net.minecraft.client.Minecraft;

import net.minecraft.client.player.LocalPlayer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Nullable
	public LocalPlayer player;

	@Shadow
	@Nullable
	public HitResult hitResult;

	@Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
	private void onEmptyClick(CallbackInfo ci, @Local(index = 5) ItemStack itemStack, @Local(index = 4) InteractionHand hand) {
		if (itemStack.isEmpty() && (this.hitResult == null || this.hitResult.getType() == HitResult.Type.MISS))
			EntityHooks.onEmptyClick(this.player, hand);
	}

	@Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V", shift = At.Shift.AFTER))
	private void leftClickEmpty(CallbackInfoReturnable<Boolean> cir) {
		EntityHooks.onEmptyLeftClick(this.player);
	}
}
