package io.github.fabricators_of_create.porting_lib.entity.extensions;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;

public interface EntityExtensions {
	/**
	 * All entities have an NBT tag where mods can store any data they need. This data is saved and persistent.
	 */
	default CompoundTag getCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * Once called, any items dropped by this entity will be intercepted and not spawned. They will be collected for
	 * later retrieval by {@link EntityExtensions#getCapturedDrops()} or {@link EntityExtensions#finishCapturingDrops()}.
	 */
	default void startCapturingDrops() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * @return all items that have been captured so far, or null if capturing was not started
	 */
	default List<ItemEntity> getCapturedDrops() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * Stop capturing dropped items.
	 * @return all items captured, or null if capturing was not started
	 */
	default List<ItemEntity> finishCapturingDrops() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * If a rider of this entity can interact with this entity. Should return true on the
	 * ridden entity if so.
	 *
	 * @return if the entity can be interacted with from a rider
	 */
	default boolean canRiderInteract() {
		return false;
	}
}
