package io.github.fabricators_of_create.porting_lib.attributes.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface PlayerAttributesExtensions {
	/**
	 * Utility check to see if the player is close enough to a target entity. Uses "eye-to-closest-corner" checks.
	 * @param entity The entity being checked against
	 * @param dist The max distance allowed
	 * @return If the eye-to-center distance between this player and the passed entity is less than dist.
	 * @implNote This method inflates the bounding box by the pick radius, which differs from vanilla. But vanilla doesn't use the pick radius, the only entity with > 0 is AbstractHurtingProjectile.
	 */
	default boolean isCloseEnough(Entity entity, double dist) {
		Vec3 eye = ((Player) this).getEyePosition();
		AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
		return aabb.distanceToSqr(eye) < dist * dist;
	}
}
