package io.github.fabricators_of_create.porting_lib.resources.injections;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import net.minecraft.core.HolderLookup;

public interface ReloadableServerResourcesInjection {
	default ICondition.IContext port_lib$getConditionContext() {
		throw PortingLib.createMixinException("ReloadableServerResourcesInjection.port_lib$getConditionContext()");
	}

	default HolderLookup.Provider port_lib$getRegistryLookup() {
		throw PortingLib.createMixinException("ReloadableServerResourcesInjection.port_lib$getRegistryLookup");
	}
}
