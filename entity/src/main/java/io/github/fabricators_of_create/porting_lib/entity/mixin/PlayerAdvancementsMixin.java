package io.github.fabricators_of_create.porting_lib.entity.mixin;

import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.advancements.Advancement;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
	@Shadow
	private ServerPlayer player;

	@Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"))
	public void onAwardAdvancement(Advancement advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir) {
		PlayerEvents.ADVANCEMENT_GRANT.invoker().onGrantAdvancement(this.player, advancement);
	}
}
