package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.BaseSpawnerAccessor;
import net.minecraft.util.collection.DataPool;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;

public final class AbstractSpawnerHelper {
	public static DataPool<MobSpawnerEntry> getPotentialSpawns(MobSpawnerLogic abstractSpawner) {
		return get(abstractSpawner).port_lib$getSpawnPotentials();
	}

	public static MobSpawnerEntry getSpawnData(MobSpawnerLogic abstractSpawner) {
		return get(abstractSpawner).port_lib$getNextSpawnData();
	}

	private static BaseSpawnerAccessor get(MobSpawnerLogic abstractSpawner) {
		return MixinHelper.cast(abstractSpawner);
	}

	private AbstractSpawnerHelper() {}
}
