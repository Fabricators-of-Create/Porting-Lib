package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.ClientPlayerNetworkCloneCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceKey;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {

	protected ClientPacketListenerMixin(Minecraft client, Connection connection, CommonListenerCookie commonListenerCookie) {
		super(client, connection, commonListenerCookie);
	}

	@Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addPlayer(ILnet/minecraft/client/player/AbstractClientPlayer;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onClientPlayerRespawn(ClientboundRespawnPacket packet, CallbackInfo ci, CommonPlayerSpawnInfo commonPlayerSpawnInfo, ResourceKey<Level> level, Holder<DimensionType> holder, LocalPlayer oldPlayer, int i, LocalPlayer newPlayer) {
		ClientPlayerNetworkCloneCallback.EVENT.invoker().onPlayerRespawn(this.minecraft.gameMode, commonPlayerSpawnInfo, oldPlayer, newPlayer, newPlayer.connection.getConnection());
	}
}
