package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.OnDatapackSyncCallback;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
	@Inject(
			method = "placeNewPlayer",
			at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/protocol/game/ClientboundSetCarriedItemPacket;<init>(I)V")
	)
	private void port_lib$placeNewPlayer(Connection netManager, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
		OnDatapackSyncCallback.EVENT.invoker().onDatapackSync((PlayerList) (Object) this, player);
	}

	@Inject(
			method = "reloadResources",
			at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V")
	)
	private void port_lib$placeNewPlayer(CallbackInfo ci) {
		OnDatapackSyncCallback.EVENT.invoker().onDatapackSync((PlayerList) (Object) this, null);
	}

	@Inject(method = "placeNewPlayer", at = @At("TAIL"))
	private void onPlayerLoggedIn(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie cookie, CallbackInfo ci) {
		PlayerEvents.LOGGED_IN.invoker().handleConnection(serverPlayer);
	}

	@Inject(method = "respawn", at = @At("HEAD"))
	private void onPlayerLoggedOut(ServerPlayer serverPlayer, boolean bl, CallbackInfoReturnable<ServerPlayer> cir) {
		PlayerEvents.LOGGED_OUT.invoker().handleConnection(serverPlayer);
	}
}
