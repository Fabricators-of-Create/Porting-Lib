package io.github.fabricators_of_create.porting_lib.models.injections;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.client.resources.model.ModelBakery;

public interface ModelManagerInjection {
	default ModelBakery port_lib$getModelBakery() {
		throw PortingLib.createMixinException("ModelManagerInjection#getModelBakery");
	}
}
