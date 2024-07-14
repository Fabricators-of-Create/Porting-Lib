package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityTeleportEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public abstract class EnderManMixin extends Monster {
	protected EnderManMixin(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@WrapOperation(method = "teleport(DDD)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/EnderMan;randomTeleport(DDDZ)Z"))
	private boolean onEnderTeleport(EnderMan instance, double x, double y, double z, boolean fireEvent, Operation<Boolean> original) {
		EntityTeleportEvent.EnderEntity event = EntityHooks.onEnderTeleport(instance, x, y, z);
		if (!event.isCanceled())
			return original.call(instance, event.getTargetX(), event.getTargetY(), event.getTargetZ(), fireEvent);
		return false;
	}
}
