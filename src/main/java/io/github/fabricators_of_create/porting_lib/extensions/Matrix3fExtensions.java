package io.github.fabricators_of_create.porting_lib.extensions;

import org.jetbrains.annotations.NotNull;

import com.mojang.math.Matrix3f;

public interface Matrix3fExtensions {
	float[] port_lib$writeMatrix();

	void port_lib$set(@NotNull Matrix3f other);
}
