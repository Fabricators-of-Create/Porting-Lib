package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.extensions.EntityExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.EntityAccessor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class EntityHelper {
	public static final String EXTRA_DATA_KEY = "create_ExtraEntityData";

	public static CompoundTag getExtraCustomData(Entity entity) {
		return ((EntityExtensions) entity).port_lib$getExtraCustomData();
	}

	public static String getEntityString(Entity entity) {
		return ((EntityAccessor) entity).port_lib$getEntityString();
	}

	private EntityHelper() {}
}
