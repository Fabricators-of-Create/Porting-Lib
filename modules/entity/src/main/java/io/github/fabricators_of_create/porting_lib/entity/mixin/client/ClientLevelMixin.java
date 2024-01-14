package io.github.fabricators_of_create.porting_lib.entity.mixin.client;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
	@Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
	public void port_lib$addEntityEvent(Entity entity, CallbackInfo ci) {
		if (EntityEvents.ON_JOIN_WORLD.invoker().onJoinWorld(entity, (Level) (Object) this, false))
			ci.cancel();
	}
}
