package io.github.fabricators_of_create.porting_lib.fake_players.mixin.common;

import io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.advancements.Advancement;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {
	@Shadow
	private ServerPlayer player;

	@Inject(method = "award", at = @At("HEAD"), cancellable = true)
	private void noAdvancementsForFakePlayers(Advancement advancement, String criterionKey, CallbackInfoReturnable<Boolean> cir) {
		if (player instanceof FakePlayer)
			cir.setReturnValue(false);
	}
}
