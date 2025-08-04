package io.github.fabricators_of_create.porting_lib.item.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;

public interface ItemInjection {
	default boolean port_lib$canRepair() {
		throw PortingLib.createMixinException("ItemInjection#canRepair");
	}
}
