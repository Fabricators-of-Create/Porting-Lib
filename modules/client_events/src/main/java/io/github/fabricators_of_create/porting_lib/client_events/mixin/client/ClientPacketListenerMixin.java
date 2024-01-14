package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.ClientPlayerNetworkCloneCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
	protected ClientPacketListenerMixin(Minecraft client, Connection connection, CommonListenerCookie commonListenerCookie) {
		super(client, connection, commonListenerCookie);
	}

	@ModifyArg(
			method = "handleRespawn",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientLevel;addEntity(Lnet/minecraft/world/entity/Entity;)V"
			)
	)
	private Entity onClientPlayerRespawn(Entity entity, @Local(ordinal = 0) LocalPlayer oldPlayer) {
		LocalPlayer newPlayer = (LocalPlayer) entity;
		ClientPlayerNetworkCloneCallback.EVENT.invoker().onPlayerRespawn(this.minecraft.gameMode, oldPlayer, newPlayer, this.connection);
		return entity;
	}
}
