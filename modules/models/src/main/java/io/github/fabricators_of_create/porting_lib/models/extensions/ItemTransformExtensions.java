package io.github.fabricators_of_create.porting_lib.models.extensions;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;

import org.joml.Vector3f;

public interface ItemTransformExtensions {
	default Vector3f getRightRotation() {
		throw PortingLib.createMixinException("ItemTransformExtensions.getRightRotation() not implemented");
	}

	default void setRightRotation(Vector3f rightRotation) {
		throw PortingLib.createMixinException("ItemTransformExtensions.setRightRotation(Vector3f) not implemented");
	}
}
