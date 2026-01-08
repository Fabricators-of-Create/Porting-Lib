package io.github.fabricators_of_create.porting_lib.extensions.common;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.resources.Identifier;

public interface IdentifierExtension {
	default int compareNamespaced(Identifier o) {
		throw PortingLib.createMixinException("IdentifierExtension#compareNamespaced");
	}
}
