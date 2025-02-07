package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.AdvancementCallback;
import io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementsMixin {
	@Shadow
	private ServerPlayerEntity player;

	@Inject(method = "award", at = @At("HEAD"), cancellable = true)
	private void noAdvancementsForFakePlayers(Advancement advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir) {
		if (player instanceof FakePlayer)
			cir.setReturnValue(false);
	}

	@Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"))
	public void port_lib$onAwardAdvancement(Advancement advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir) {
		AdvancementCallback.EVENT.invoker().onAdvancement(this.player, advancement);
	}
}
