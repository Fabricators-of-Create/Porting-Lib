package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.event.common.OnDatapackSyncCallback;
import io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer;
import io.github.fabricators_of_create.porting_lib.util.UsernameCache;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerListMixin {
	@Inject(
			method = "placeNewPlayer",
			at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/network/protocol/game/ClientboundSetCarriedItemPacket;<init>(I)V")
	)
	private void port_lib$placeNewPlayer(ClientConnection netManager, ServerPlayerEntity player, CallbackInfo ci) {
		OnDatapackSyncCallback.EVENT.invoker().onDatapackSync((PlayerManager) (Object) this, player);
	}

	@Inject(
			method = "reloadResources",
			at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V")
	)
	private void port_lib$placeNewPlayer(CallbackInfo ci) {
		OnDatapackSyncCallback.EVENT.invoker().onDatapackSync((PlayerManager) (Object) this, null);
	}

	@Inject(method = "placeNewPlayer", at = @At("TAIL"))
	private void setPlayerUsername(ClientConnection netManager, ServerPlayerEntity player, CallbackInfo ci) {
		UsernameCache.setUsername(player.getUuid(), player.getGameProfile().getName());
	}

	@WrapWithCondition(
			method = "getPlayerAdvancements",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerAdvancements;setPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"
			)
	)
	private boolean noAdvancementsForFakePlayers(PlayerAdvancementTracker advancements, ServerPlayerEntity player) {
		return !(player instanceof FakePlayer);
	}
}
