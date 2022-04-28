package io.github.fabricators_of_create.porting_lib.extensions;

import java.util.Collection;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;

public interface EntityExtensions {
	default CompoundTag getExtraCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> captureDrops() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
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
