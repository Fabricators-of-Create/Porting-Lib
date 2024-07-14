package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(method = "addPairing", at = @At("TAIL"))
	private void addPairing(ServerPlayer player, CallbackInfo ci) {
		EntityHooks.onStartEntityTracking(this.entity, player);
	}

	@Inject(method = "removePairing", at = @At("TAIL"))
	private void onStopEntityTracking(ServerPlayer player, CallbackInfo ci) {
		EntityHooks.onStopEntityTracking(this.entity, player);
	}

	@Inject(method = "sendPairingData", at = @At(
			value = "INVOKE",
			target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
			shift = At.Shift.AFTER,
			ordinal = 0
	))
	private void sendComplexSpawnData(ServerPlayer serverPlayer, Consumer<Packet<?>> consumer, CallbackInfo ci) {
		this.entity.sendPairingData(serverPlayer, customPacketPayload -> consumer.accept(new ClientboundCustomPayloadPacket(customPacketPayload)));
	}
}
