package io.github.fabricators_of_create.porting_lib.entity.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.entity.EffectCure;

import java.util.Optional;
import java.util.Set;

public interface MobEffectInstance$DetailsInjection {
	default Optional<Set<EffectCure>> port_lib$getCures() {
		throw PortingLib.createMixinException("MobEffectInstance$DetailsInjection.port_lib$getCures()");
	}

	default void port_lib$setCures(Optional<Set<EffectCure>> cures) {
		throw PortingLib.createMixinException("MobEffectInstance$DetailsInjection.port_lib$setCures()");
	}
}
