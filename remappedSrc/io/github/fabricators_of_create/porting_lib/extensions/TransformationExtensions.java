package io.github.fabricators_of_create.porting_lib.extensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public interface TransformationExtensions {
	/**
	 * Apply this transformation to a different origin.
	 * Can be used for switching between coordinate systems.
	 * Parameter is relative to the current origin.
	 */
	default AffineTransformation applyOrigin(Vec3f origin) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Matrix3f getNormalMatrix() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	@Environment(EnvType.CLIENT)
	default void push(MatrixStack stack) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void transformPosition(Vector4f position) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Direction rotateTransform(Direction facing) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default boolean isIdentity() {
		return this.equals(AffineTransformation.identity());
	}

	default void transformNormal(Vec3f normal) {
		normal.transform(getNormalMatrix());
		normal.normalize();
	}

	/**
	 * convert transformation from assuming center-block system to opposing-corner-block system
	 */
	default AffineTransformation blockCenterToCorner() {
		return applyOrigin(new Vec3f(.5f, .5f, .5f));
	}

	/**
	 * convert transformation from assuming opposing-corner-block system to center-block system
	 */
	default AffineTransformation blockCornerToCenter() {
		return applyOrigin(new Vec3f(-.5f, -.5f, -.5f));
	}
}
