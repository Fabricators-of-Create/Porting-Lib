package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.OnDatapackSyncCallback;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;

import net.minecraft.world.level.storage.PlayerDataStorage;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
	@Shadow
	@Final
	private PlayerDataStorage playerIo;

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
		EntityHooks.firePlayerLoggedIn(serverPlayer);
	}

	@Inject(method = "remove", at = @At("HEAD"))
	private void onPlayerLoggedOut(ServerPlayer serverPlayer, CallbackInfo ci) {
		EntityHooks.firePlayerLoggedOut(serverPlayer);
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;)V", shift = At.Shift.AFTER))
	private void onPlayerLoad(ServerPlayer player, CallbackInfoReturnable<Optional<CompoundTag>> cir) {
		EntityHooks.firePlayerLoadingEvent(player, this.playerIo, player.getUUID().toString());
	}
}
