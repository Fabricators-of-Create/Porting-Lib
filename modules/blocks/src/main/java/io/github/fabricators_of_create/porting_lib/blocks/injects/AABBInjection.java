package io.github.fabricators_of_create.porting_lib.blocks.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;

public interface AABBInjection {
	/**
	 * {@return true if this AABB is infinite in all directions}
	 */
	default boolean port_lib$isInfinite() {
		throw PortingLib.createMixinException("AABBInjection.port_lib$isInfinite()");
	}
}
