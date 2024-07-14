package io.github.fabricators_of_create.porting_lib.entity.ext;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.entity.EffectCure;

public interface LivingEntityExt {
	/**
	 * Removes all potion effects that have the given {@link EffectCure} in their set of cures
	 * @param cure the EffectCure being used
	 */
	default boolean removeEffectsCuredBy(EffectCure cure) {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support removeEffectsCuredBy(EffectCure)");
	}
}
