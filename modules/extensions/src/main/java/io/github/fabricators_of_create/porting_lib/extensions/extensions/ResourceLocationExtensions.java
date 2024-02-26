package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationExtensions {
	default int compareNamespaced(ResourceLocation o) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
