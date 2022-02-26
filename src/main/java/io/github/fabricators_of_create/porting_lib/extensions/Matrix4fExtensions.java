package io.github.fabricators_of_create.porting_lib.extensions;

import org.jetbrains.annotations.Contract;

public interface Matrix4fExtensions {
	@Contract(mutates = "this")
	void port_lib$fromFloatArray(float[] floats);
}
