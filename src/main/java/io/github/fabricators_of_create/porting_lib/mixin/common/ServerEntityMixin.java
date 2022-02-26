package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.EntityStartTrackingTailCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(method = "addPairing", at = @At("TAIL"))
	private void port_lib$addPairing(ServerPlayer player, CallbackInfo ci) {
		EntityStartTrackingTailCallback.EVENT.invoker().onTrackingStart(entity, player);
	}
}
