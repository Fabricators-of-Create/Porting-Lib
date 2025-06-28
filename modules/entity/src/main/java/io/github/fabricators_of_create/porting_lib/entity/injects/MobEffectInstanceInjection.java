package io.github.fabricators_of_create.porting_lib.entity.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.entity.EffectCure;

import java.util.Set;

public interface MobEffectInstanceInjection {
	default Set<EffectCure> getCures() {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support getCures()");
	}
}
