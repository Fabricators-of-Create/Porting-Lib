package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.util.UsernameCache;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
	@Inject(method = "placeNewPlayer", at = @At("TAIL"))
	private void setPlayerUsername(Connection netManager, ServerPlayer player, int latency, CallbackInfo ci) {
		UsernameCache.setUsername(player.getUUID(), player.getGameProfile().getName());
	}
}
