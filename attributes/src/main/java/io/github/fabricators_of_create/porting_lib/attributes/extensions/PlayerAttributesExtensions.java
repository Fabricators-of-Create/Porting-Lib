package io.github.fabricators_of_create.porting_lib.attributes.extensions;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface PlayerAttributesExtensions {
	private Player self() {
		return (Player) this;
	}

	default double getBlockReach() {
		double reach = self().getAttributeValue(PortingLibAttributes.BLOCK_REACH);
		return reach == 0 ? 0 : reach + (self().isCreative() ? 0.5 : 0);
	}

	/**
	 * Checks if the player can reach a block.<br>
	 * On the server, additional padding is added to account for movement/lag.
	 * @param pos The position being range-checked.
	 * @param padding Extra validation distance.
	 * @return If the player can interact with this location.
	 */
	default boolean canReach(BlockPos pos, double padding) {
		double reach = this.getBlockReach() + padding;
		return self().getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) < reach * reach;
	}

	/**
	 * The entity reach is increased by 3 for creative players, unless it is currently zero, which disables attacks and entity interactions.
	 * @return The entity reach of this player.
	 */
	default double getEntityReach() {
		double range = self().getAttributeValue(PortingLibAttributes.ENTITY_REACH);
		return range == 0 ? 0 : range + (self().isCreative() ? 3 : 0);
	}

	/**
	 * Checks if the player can reach an entity by targeting the passed vector.<br>
	 * On the server, additional padding is added to account for movement/lag.
	 * @param entityHitVec The vector being range-checked.
	 * @param padding Extra validation distance.
	 * @return If the player can attack the entity.
	 * @apiNote Do not use for block checks, as this method uses {@link #getEntityReach()}
	 */
	default boolean canReach(Vec3 entityHitVec, double padding) {
		return self().getEyePosition().closerThan(entityHitVec, getEntityReach() + padding);
	}

	/**
	 * Checks if the player can reach an entity.<br>
	 * On the server, additional padding is added to account for movement/lag.
	 * @param entity The entity being range-checked.
	 * @param padding Extra validation distance.
	 * @return If the player can attack the passed entity.
	 * @apiNote Prefer using {@link #canReach(Vec3, double)} if you have a {@link HitResult} available.
	 */
	default boolean canReach(Entity entity, double padding) {
		return isCloseEnough(entity, getEntityReach() + padding);
	}

	/**
	 * Utility check to see if the player is close enough to a target entity. Uses "eye-to-closest-corner" checks.
	 * @param entity The entity being checked against
	 * @param dist The max distance allowed
	 * @return If the eye-to-center distance between this player and the passed entity is less than dist.
	 * @implNote This method inflates the bounding box by the pick radius, which differs from vanilla. But vanilla doesn't use the pick radius, the only entity with > 0 is AbstractHurtingProjectile.
	 */
	default boolean isCloseEnough(Entity entity, double dist) {
		Vec3 eye = self().getEyePosition();
		AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
		return aabb.distanceToSqr(eye) < dist * dist;
	}
}
