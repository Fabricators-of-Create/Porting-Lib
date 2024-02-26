package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.common.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.BiomeManagerAccessor;

import net.minecraft.world.level.biome.BiomeManager;

public final class BiomeManagerHelper {
	public static long getSeed(BiomeManager biomeManager) {
		return get(biomeManager).port_lib$getBiomeZoomSeed();
	}

	private static BiomeManagerAccessor get(BiomeManager biomeManager) {
		return MixinHelper.cast(biomeManager);
	}

	private BiomeManagerHelper() {}
}
