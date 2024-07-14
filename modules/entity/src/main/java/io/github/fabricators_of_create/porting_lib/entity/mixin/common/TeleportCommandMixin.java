package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import java.util.Set;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityTeleportEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;

@Mixin(TeleportCommand.class)
public abstract class TeleportCommandMixin {
	@Inject(method = "performTeleport", at = @At("HEAD"), cancellable = true)
	private static void onEntityTeleportCommand(CommandSourceStack source, Entity target, ServerLevel world, double x, double y, double z, Set<RelativeMovement> movementFlags, float yaw, float pitch, TeleportCommand.LookAt facingLocation, CallbackInfo ci) {
		EntityTeleportEvent.TeleportCommand event = EntityHooks.onEntityTeleportCommand(target, x, y, z);
		if (event.isCanceled())
			ci.cancel();
	}
}
