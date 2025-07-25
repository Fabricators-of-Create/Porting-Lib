package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.RemovalFromWorldListener;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityLeaveLevelEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/level/ServerLevel$EntityCallbacks")
public abstract class ServerLevel$EntityCallbacksMixin {
	@Shadow
	@Final
	private ServerLevel field_26936;

	@Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
	private void port_lib$onTrackingEnd(Entity entity, CallbackInfo ci) {
		if (entity instanceof RemovalFromWorldListener listener) {
			listener.onRemovedFromWorld();
		}

		(new EntityLeaveLevelEvent(entity, field_26936)).sendEvent();
	}
}
