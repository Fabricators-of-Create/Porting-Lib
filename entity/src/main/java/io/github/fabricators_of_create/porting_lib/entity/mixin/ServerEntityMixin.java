package io.github.fabricators_of_create.porting_lib.entity.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.entity.events.AdditionalEntityTrackingEvents;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
	@Shadow
	@Final
	private Entity entity;

	@Inject(method = "addPairing", at = @At("TAIL"))
	private void afterTrackingStart(ServerPlayer player, CallbackInfo ci) {
		AdditionalEntityTrackingEvents.AFTER_START_TRACKING.invoker().afterStartTracking(entity, player);
	}

	@Inject(method = "addPairing", at = @At("TAIL"))
	private void beforeTrackingStop(ServerPlayer player, CallbackInfo ci) {
		AdditionalEntityTrackingEvents.BEFORE_STOP_TRACKING.invoker().beforeStopTracking(entity, player);
	}
}
