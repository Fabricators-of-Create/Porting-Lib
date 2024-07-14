package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityTeleportEvent;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(SpreadPlayersCommand.class)
public abstract class SpreadPlayersCommandMixin {
	@WrapOperation(method = "setPlayerPositions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"))
	private static boolean onSpreadPlayers(Entity instance, ServerLevel serverLevel, double x, double y, double z, Set<RelativeMovement> set, float yRot, float xRot, Operation<Boolean> original) {
		EntityTeleportEvent.SpreadPlayersCommand event = EntityHooks.onEntityTeleportSpreadPlayersCommand(instance, x, y, z);

		if (!event.isCanceled()) {
			return original.call(instance,
					serverLevel,
					event.getTargetX(),
					event.getTargetY(),
					event.getTargetZ(),
					set,
					yRot,
					xRot
			);
		}
		return !event.isCanceled();
	}
}
