package io.github.fabricators_of_create.porting_lib.extensions;

import java.util.Collection;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;

public interface EntityExtensions {
	CompoundTag getExtraCustomData();

	Collection<ItemEntity> captureDrops();

	Collection<ItemEntity> captureDrops(Collection<ItemEntity> value);
}
