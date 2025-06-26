package io.github.fabricators_of_create.porting_lib.common.extensions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public interface CustomSortOrderMobEffect {
	/**
	 * Used for determining {@link MobEffect} sort order in GUIs.
	 * Defaults to the {@link MobEffect}'s liquid color.
	 *
	 * @param effectInstance the {@link MobEffectInstance} containing this {@link MobEffect}
	 * @return a value used to sort {@link MobEffect}s in GUIs
	 */
	default int getSortOrder(MobEffectInstance effectInstance) {
		return ((MobEffect) this).getColor();
	}
}
