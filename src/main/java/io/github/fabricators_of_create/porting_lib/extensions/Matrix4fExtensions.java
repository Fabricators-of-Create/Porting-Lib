package io.github.fabricators_of_create.porting_lib.extensions;

import com.mojang.math.Matrix4f;

import org.jetbrains.annotations.Contract;

public interface Matrix4fExtensions {
	@Contract(mutates = "this")
	default void fromFloatArray(float[] floats) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void setTranslation(float x, float y, float z) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void multiplyBackward(Matrix4f other) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
