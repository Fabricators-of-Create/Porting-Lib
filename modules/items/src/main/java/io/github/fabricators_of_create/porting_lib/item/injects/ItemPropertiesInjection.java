package io.github.fabricators_of_create.porting_lib.item.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.world.item.Item;

public interface ItemPropertiesInjection {
	default Item.Properties port_lib$setNoRepair() {
		throw PortingLib.createMixinException("ItemPropertiesInjection#port_lib$setNoRepair");
	}

	default boolean port_lib$getNoRepair() {
		throw PortingLib.createMixinException("ItemPropertiesInjection#port_lib$getNoRepair");
	}
}
