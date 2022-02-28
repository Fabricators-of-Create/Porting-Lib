package io.github.fabricators_of_create.porting_lib.extensions;

import com.mojang.math.Matrix4f;

import org.jetbrains.annotations.Contract;

public interface Matrix4fExtensions {
	@Contract(mutates = "this")
	void port_lib$fromFloatArray(float[] floats);

	void port_lib$setTranslation(float x, float y, float z);

	void port_lib$multiplyBackward(Matrix4f other);
}
