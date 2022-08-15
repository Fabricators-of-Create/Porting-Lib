package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.OnDatapackSyncCallback;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
	@Inject(
			method = "placeNewPlayer",
			at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/network/protocol/game/ClientboundSetCarriedItemPacket;<init>(I)V")
	)
	private void port_lib$placeNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
		OnDatapackSyncCallback.EVENT.invoker().onDatapackSync((PlayerList) (Object) this, player);
	}

	@Inject(
			method = "reloadResources",
			at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V")
	)
	private void port_lib$placeNewPlayer(CallbackInfo ci) {
		OnDatapackSyncCallback.EVENT.invoker().onDatapackSync((PlayerList) (Object) this, null);
	}
}
