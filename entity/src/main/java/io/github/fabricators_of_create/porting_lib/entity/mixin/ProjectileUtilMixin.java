package io.github.fabricators_of_create.porting_lib.entity.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {

	@WrapOperation(
			method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;getRootVehicle()Lnet/minecraft/world/entity/Entity;",
					ordinal = 0
			)
	)
	private static Entity checkRiderCanInteract(Entity foundEntity, Operation<Entity> original,
												Entity origin, Vec3 min, Vec3 max, AABB box, Predicate<Entity> predicate, double squaredDistance) {
		Entity foundVehicle = original.call(foundEntity);
		// original: foundVehicle == originVehicle
		// desired: (foundVehicle == originVehicle) && !foundEntity.canRiderInteract()
		// wrapping foundVehicle
		if (!foundEntity.canRiderInteract())
			return foundVehicle; // let == logic run normally
		return null; // == must fail, so return null. root vehicle will never be null.
	}
}
