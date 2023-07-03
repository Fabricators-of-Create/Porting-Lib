package io.github.fabricators_of_create.porting_lib.entity.mixin.teleport;

import java.util.Set;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents.EntityTeleportEvent;

import org.jetbrains.annotations.Nullable;
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
	private static void fireTeleportEvent(CommandSourceStack source, Entity target, ServerLevel world,
										  double x, double y, double z,
										  Set<RelativeMovement> movementFlags, float yaw, float pitch,
										  @Nullable TeleportCommand.LookAt facingLocation, CallbackInfo ci) {
		EntityTeleportEvent event = new EntityTeleportEvent(target, x, y, z);
		EntityMoveEvents.TELEPORT.invoker().onTeleport(event);
		if (event.isCancelled())
			ci.cancel();
	}
}
