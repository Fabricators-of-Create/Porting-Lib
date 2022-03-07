package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.event.EntityTeleportEvent;
import net.minecraft.server.commands.SpreadPlayersCommand;

import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpreadPlayersCommand.class)
public abstract class SpreadPlayersCommandMixin {
	@WrapWithCondition(method = "setPlayerPositions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportToWithTicket(DDD)V"))
	private static boolean port_lib$setPlayerPositions(Entity instance, double x, double y, double z) {
		EntityTeleportEvent event = new EntityTeleportEvent(instance, x, y, z);
		event.sendEvent();
		return !event.isCanceled();
	}
}
