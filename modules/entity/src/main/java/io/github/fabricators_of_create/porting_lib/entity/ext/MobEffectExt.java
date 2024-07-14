package io.github.fabricators_of_create.porting_lib.entity.ext;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.entity.EffectCure;
import io.github.fabricators_of_create.porting_lib.entity.EffectCures;
import io.github.fabricators_of_create.porting_lib.entity.client.MobEffectRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface MobEffectExt {
	/***
	 * Fill the given set with the {@link EffectCure}s this effect should be curable with by default
	 */
	default void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		cures.addAll(EffectCures.DEFAULT_CURES);
		if (MixinHelper.cast(this) == MobEffects.POISON.value()) {
			cures.add(EffectCures.HONEY);
		}
	}

	@Nullable
	default MobEffectRenderer getRenderer() {
		return null;
	}
}
