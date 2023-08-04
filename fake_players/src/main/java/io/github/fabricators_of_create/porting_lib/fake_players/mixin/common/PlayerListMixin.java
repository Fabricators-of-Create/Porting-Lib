package io.github.fabricators_of_create.porting_lib.fake_players.mixin.common;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

@Mixin(PlayerList.class)
public class PlayerListMixin {
	@WrapWithCondition(
			method = "getPlayerAdvancements",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerAdvancements;setPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"
			)
	)
	private boolean noAdvancementsForFakePlayers(PlayerAdvancements advancements, ServerPlayer player) {
		return !(player instanceof FakePlayer);
	}
}
