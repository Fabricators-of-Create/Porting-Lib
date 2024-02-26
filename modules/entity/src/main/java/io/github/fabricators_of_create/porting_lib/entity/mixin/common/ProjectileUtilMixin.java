package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {

	@Unique
	private static boolean port_lib$canRiderInteract = false;

	@ModifyExpressionValue(
			method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getRootVehicle()Lnet/minecraft/world/entity/Entity;", ordinal = 1)
	)
	private static Entity port_lib$rider(Entity original) {
		return port_lib$canRiderInteract ? original : null;
	}

	@Inject(
			method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getRootVehicle()Lnet/minecraft/world/entity/Entity;", ordinal = 0, shift = At.Shift.BEFORE),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void port_lib$result(Entity shooter, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, double distance, CallbackInfoReturnable<EntityHitResult> cir, Level level, double d, Entity entity, Vec3 vec3, Iterator var12, Entity entity2, AABB aABB, Optional optional, Vec3 vec32, double e) {
		port_lib$canRiderInteract = !entity2.canRiderInteract();
	}
}
