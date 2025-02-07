package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.util.Identifier;

public interface ResourceLocationExtensions {
	default int compareNamespaced(Identifier o) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
