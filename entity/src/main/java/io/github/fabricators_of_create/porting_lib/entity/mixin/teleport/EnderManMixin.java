package io.github.fabricators_of_create.porting_lib.entity.mixin.teleport;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents.EntityTeleportEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

@Mixin(EnderMan.class)
public abstract class EnderManMixin extends Monster {
	protected EnderManMixin(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@WrapOperation(
			method = "teleport(DDD)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/EnderMan;randomTeleport(DDDZ)Z"
			)
	)
	private boolean fireTeleportEvent(EnderMan self, double x, double y, double z, boolean particles,
									  Operation<Boolean> original) {
		EntityTeleportEvent event = new EntityTeleportEvent(this, x, y, z);
		EntityMoveEvents.TELEPORT.invoker().onTeleport(event);
		return !event.isCancelled() && original.call(self, x, y, z, particles);
	}
}
