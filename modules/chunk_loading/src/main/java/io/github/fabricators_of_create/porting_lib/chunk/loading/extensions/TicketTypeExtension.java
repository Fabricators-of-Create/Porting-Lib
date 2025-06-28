package io.github.fabricators_of_create.porting_lib.chunk.loading.extensions;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;

public interface TicketTypeExtension {
	default boolean port_lib$forceNaturalSpawning() {
		throw PortingLib.createMixinException("TicketTypeExtension.port_lib$forceNaturalSpawning()");
	}

	default void port_lib$setForceNaturalSpawning(boolean forceNaturalSpawning) {
		throw PortingLib.createMixinException("TicketTypeExtension.port_lib$setForceNaturalSpawning()");
	}
}
