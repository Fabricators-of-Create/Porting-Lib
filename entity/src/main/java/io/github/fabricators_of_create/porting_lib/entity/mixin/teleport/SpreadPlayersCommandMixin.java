package io.github.fabricators_of_create.porting_lib.entity.mixin.teleport;

import java.util.Set;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents.EntityTeleportEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;

@Mixin(SpreadPlayersCommand.class)
public abstract class SpreadPlayersCommandMixin {
	@WrapWithCondition(
			method = "setPlayerPositions",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"
			)
	)
	private static boolean fireTeleportEvent(Entity entity, ServerLevel level,
													   double x, double y, double z,
													   Set<RelativeMovement> set,
													   float yaw, float pitch) {
		EntityTeleportEvent event = new EntityTeleportEvent(entity, x, y, z);
		EntityMoveEvents.TELEPORT.invoker().onTeleport(event);
		return !event.isCancelled();
	}
}
