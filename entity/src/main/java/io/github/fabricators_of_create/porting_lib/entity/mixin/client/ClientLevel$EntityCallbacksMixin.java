package io.github.fabricators_of_create.porting_lib.entity.mixin.client;

import io.github.fabricators_of_create.porting_lib.entity.RemovalFromWorldListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.multiplayer.ClientLevel$EntityCallbacks")
public abstract class ClientLevel$EntityCallbacksMixin {
	@Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
	private void port_lib$onTrackingEnd(Entity entity, CallbackInfo ci) {
		if (entity instanceof RemovalFromWorldListener listener) {
			listener.onRemovedFromWorld();
		}
	}
}
