package io.github.fabricators_of_create.porting_lib.util;

import com.mojang.math.Matrix4f;

public final class Matrix4fHelper {

	public static Matrix4f fromFloatArray(float[] values) {
		Matrix4f matrix = new Matrix4f();
		matrix.fromFloatArray(values);
		return matrix;
	}

	private Matrix4fHelper() {}
}
