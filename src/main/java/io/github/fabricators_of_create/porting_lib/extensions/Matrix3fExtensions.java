package io.github.fabricators_of_create.porting_lib.extensions;

import org.jetbrains.annotations.NotNull;

import com.mojang.math.Matrix3f;

public interface Matrix3fExtensions {
	default float[] writeMatrix() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void set(@NotNull Matrix3f other) {

	}
}
