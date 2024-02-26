package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(method = "addPairing", at = @At("TAIL"))
	private void port_lib$addPairing(ServerPlayer player, CallbackInfo ci) {
		EntityEvents.START_TRACKING_TAIL.invoker().onTrackingStart(entity, player);
	}
}
