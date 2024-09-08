package io.github.fabricators_of_create.porting_lib.models.geometry.extensions;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.models.ExtraFaceData;

public interface BlockElementExt {
	default ExtraFaceData port_lib$getFaceData() {
		throw PortingLib.createMixinException("BlockElementExt.port_lib$getFaceData() not implemented");
	}

	default void port_lib$setFaceData(ExtraFaceData faceData) {
		throw PortingLib.createMixinException("BlockElementExt.port_lib$setFaceData(ExtraFaceData) not implemented");
	}
}
