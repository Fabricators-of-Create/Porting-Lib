package io.github.fabricators_of_create.porting_lib.util;

import com.mojang.math.Vector3f;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.Vector3fAccessor;

public final class Vector3fHelper {
	public static void setX(Vector3f vector, float x) {
		get(vector).port_lib$setX(x);
	}

	public static void setY(Vector3f vector, float y) {
		get(vector).port_lib$setY(y);
	}

	public static void setZ(Vector3f vector, float z) {
		get(vector).port_lib$setZ(z);
	}

	private static Vector3fAccessor get(Vector3f vector) {
		return MixinHelper.cast(vector);
	}

	private Vector3fHelper() {}
}
