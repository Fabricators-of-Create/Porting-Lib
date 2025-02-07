package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.BiomeManagerAccessor;
import net.minecraft.world.biome.source.BiomeAccess;

public final class BiomeManagerHelper {
	public static long getSeed(BiomeAccess biomeManager) {
		return get(biomeManager).port_lib$getBiomeZoomSeed();
	}

	private static BiomeManagerAccessor get(BiomeAccess biomeManager) {
		return MixinHelper.cast(biomeManager);
	}

	private BiomeManagerHelper() {}
}
