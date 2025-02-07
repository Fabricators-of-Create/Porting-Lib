package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.util.math.Matrix3f;
import org.jetbrains.annotations.NotNull;

public interface Matrix3fExtensions {
	default float[] writeMatrix() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void set(@NotNull Matrix3f other) {

	}

	default void multiplyBackward(Matrix3f other) {
		Matrix3f copy = other.copy();
		copy.multiply((Matrix3f) this);
		((Matrix3f) this).load(copy);
	}
}
