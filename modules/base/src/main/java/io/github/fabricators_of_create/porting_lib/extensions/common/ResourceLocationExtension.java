package io.github.fabricators_of_create.porting_lib.extensions.common;

import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationExtension {
	default int port_lib$compareNamespaced(ResourceLocation o) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
