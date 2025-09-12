package io.github.fabricators_of_create.porting_lib.models.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;

import org.joml.Vector3f;

public interface ItemTransformInjection {
	default Vector3f port_lib$getRightRotation() {
		throw PortingLib.createMixinException("ItemTransformExtensions.getRightRotation() not implemented");
	}

	default void port_lib$setRightRotation(Vector3f rightRotation) {
		throw PortingLib.createMixinException("ItemTransformExtensions.setRightRotation(Vector3f) not implemented");
	}
}
