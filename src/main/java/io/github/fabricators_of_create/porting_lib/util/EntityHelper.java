package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.EntityAccessor;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.LivingEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class EntityHelper {
	public static final String EXTRA_DATA_KEY = "create_ExtraEntityData";

	public static CompoundTag getExtraCustomData(Entity entity) {
		return entity.getExtraCustomData();
	}

	public static String getEntityString(Entity entity) {
		return ((EntityAccessor) entity).port_lib$getEntityString();
	}

	public static BlockPos getLastPos(LivingEntity entity) {
		return ((LivingEntityAccessor) entity).port_lib$lastPos();
	}

	private EntityHelper() {}
}
