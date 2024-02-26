package io.github.fabricators_of_create.porting_lib.entity.mixin.client;

import io.github.fabricators_of_create.porting_lib.entity.events.PlayerInteractionEvents;
import net.minecraft.client.Minecraft;

import net.minecraft.client.player.LocalPlayer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Nullable
	public LocalPlayer player;

	@Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V", shift = At.Shift.AFTER))
	private void leftClickEmpty(CallbackInfoReturnable<Boolean> cir) {
		new PlayerInteractionEvents.LeftClickEmpty(this.player).sendEvent();
	}
}
