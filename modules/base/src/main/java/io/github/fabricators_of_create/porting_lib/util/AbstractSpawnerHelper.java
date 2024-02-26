package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.common.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.BaseSpawnerAccessor;

import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;

public final class AbstractSpawnerHelper {
	public static SimpleWeightedRandomList<SpawnData> getPotentialSpawns(BaseSpawner abstractSpawner) {
		return get(abstractSpawner).port_lib$getSpawnPotentials();
	}

	public static SpawnData getSpawnData(BaseSpawner abstractSpawner) {
		return get(abstractSpawner).port_lib$getNextSpawnData();
	}

	private static BaseSpawnerAccessor get(BaseSpawner abstractSpawner) {
		return MixinHelper.cast(abstractSpawner);
	}

	private AbstractSpawnerHelper() {}
}
