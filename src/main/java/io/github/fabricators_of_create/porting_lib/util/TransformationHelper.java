package io.github.fabricators_of_create.porting_lib.util;

import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.util.Mth;

public final class TransformationHelper {
	private static final double THRESHOLD = 0.9995;

	@Deprecated
	@Environment(EnvType.CLIENT)
	// TODO: this is used in 3 places, not a trivial refactor. Needs investigating. -C
	public static Transformation toTransformation(ItemTransform transform) {
		if (transform.equals(ItemTransform.NO_TRANSFORM)) return Transformation.identity();

		return new Transformation(transform.translation, quatFromXYZ(transform.rotation, true), transform.scale, null);
	}

	public static Quaternion quatFromXYZ(Vector3f xyz, boolean degrees) {
		return new Quaternion(xyz.x(), xyz.y(), xyz.z(), degrees);
	}

	public static Quaternion quatFromXYZ(float[] xyz, boolean degrees) {
		return new Quaternion(xyz[0], xyz[1], xyz[2], degrees);
	}

	public static Quaternion makeQuaternion(float[] values) {
		return new Quaternion(values[0], values[1], values[2], values[3]);
	}

	public static Vector3f lerp(Vector3f from, Vector3f to, float progress) {
		Vector3f res = from.copy();
		res.lerp(to, progress);
		return res;
	}

	public static Quaternion slerp(Quaternion v0, Quaternion v1, float t) {
		// From https://en.wikipedia.org/w/index.php?title=Slerp&oldid=928959428
		// License: CC BY-SA 3.0 https://creativecommons.org/licenses/by-sa/3.0/

		// Compute the cosine of the angle between the two vectors.
		// If the dot product is negative, slerp won't take
		// the shorter path. Note that v1 and -v1 are equivalent when
		// the negation is applied to all four components. Fix by
		// reversing one quaternion.
		float dot = v0.i() * v1.i() + v0.j() * v1.j() + v0.k() * v1.k() + v0.r() * v1.r();
		if (dot < 0.0f) {
			v1 = new Quaternion(-v1.i(), -v1.j(), -v1.k(), -v1.r());
			dot = -dot;
		}

		// If the inputs are too close for comfort, linearly interpolate
		// and normalize the result.
		if (dot > THRESHOLD) {
			float x = Mth.lerp(t, v0.i(), v1.i());
			float y = Mth.lerp(t, v0.j(), v1.j());
			float z = Mth.lerp(t, v0.k(), v1.k());
			float w = Mth.lerp(t, v0.r(), v1.r());
			return new Quaternion(x, y, z, w);
		}

		// Since dot is in range [0, DOT_THRESHOLD], acos is safe
		float angle01 = (float) Math.acos(dot);
		float angle0t = angle01 * t;
		float sin0t = Mth.sin(angle0t);
		float sin01 = Mth.sin(angle01);
		float sin1t = Mth.sin(angle01 - angle0t);

		float s1 = sin0t / sin01;
		float s0 = sin1t / sin01;

		return new Quaternion(
				s0 * v0.i() + s1 * v1.i(),
				s0 * v0.j() + s1 * v1.j(),
				s0 * v0.k() + s1 * v1.k(),
				s0 * v0.r() + s1 * v1.r()
		);
	}

	public static Transformation slerp(Transformation one, Transformation that, float progress) {
		return new Transformation(
				lerp(one.getTranslation(), that.getTranslation(), progress),
				slerp(one.getLeftRotation(), that.getLeftRotation(), progress),
				lerp(one.getScale(), that.getScale(), progress),
				slerp(one.getRightRotation(), that.getRightRotation(), progress)
		);
	}

	public static boolean epsilonEquals(Vector4f v1, Vector4f v2, float epsilon) {
		return Mth.abs(v1.x() - v2.x()) < epsilon &&
				Mth.abs(v1.y() - v2.y()) < epsilon &&
				Mth.abs(v1.z() - v2.z()) < epsilon &&
				Mth.abs(v1.w() - v2.w()) < epsilon;
	}
}
