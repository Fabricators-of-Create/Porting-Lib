package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.math.Matrix4f;
import io.github.fabricators_of_create.porting_lib.extensions.Matrix4fExtensions;

import org.spongepowered.asm.mixin.Unique;

@Mixin(Matrix4f.class)
public abstract class Matrix4fMixin implements Matrix4fExtensions {
	@Shadow
	protected float m00;
	@Shadow
	protected float m01;
	@Shadow
	protected float m02;
	@Shadow
	protected float m03;
	@Shadow
	protected float m10;
	@Shadow
	protected float m11;
	@Shadow
	protected float m12;
	@Shadow
	protected float m13;
	@Shadow
	protected float m20;
	@Shadow
	protected float m21;
	@Shadow
	protected float m22;
	@Shadow
	protected float m23;
	@Shadow
	protected float m30;
	@Shadow
	protected float m31;
	@Shadow
	protected float m32;
	@Shadow
	protected float m33;

	@Shadow
	public abstract void load(Matrix4f other);

	@Override
	@Contract(mutates = "this")
	public void port_lib$fromFloatArray(float[] floats) {
		m00 = floats[0];
		m01 = floats[1];
		m02 = floats[2];
		m03 = floats[3];

		m10 = floats[4];
		m11 = floats[5];
		m12 = floats[6];
		m13 = floats[7];

		m20 = floats[8];
		m21 = floats[9];
		m22 = floats[10];
		m23 = floats[11];

		m30 = floats[12];
		m31 = floats[13];
		m32 = floats[14];
		m33 = floats[15];
	}

	@Unique
	@Override
	public void port_lib$setTranslation(float x, float y, float z) {
		m00 = 1.0F;
		m11 = 1.0F;
		m22 = 1.0F;
		m33 = 1.0F;
		m03 = x;
		m13 = y;
		m23 = z;
	}

	@Unique
	@Override
	public void port_lib$multiplyBackward(Matrix4f other) {
		Matrix4f copy = other.copy();
		copy.multiply((Matrix4f) (Object) this);
		this.load(copy);
	}
}
