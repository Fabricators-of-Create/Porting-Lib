package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import io.github.fabricators_of_create.porting_lib.extensions.TransformationExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;

@Mixin(Transformation.class)
public abstract class TransformationMixin implements TransformationExtensions {
	@Shadow
	@Final
	private Matrix4f matrix;

	@Shadow
	public abstract Vector3f getTranslation();

	@Shadow
	public abstract Quaternion getRightRotation();

	@Shadow
	public abstract Vector3f getScale();

	@Shadow
	public abstract Quaternion getLeftRotation();

	@Shadow
	public abstract Matrix4f getMatrix();

	@Unique
	private Matrix3f normalTransform = null;

	@Override
	public Matrix3f getNormalMatrix() {
		port_lib$checkNormalTransform();
		return normalTransform;
	}

	@Unique
	private void port_lib$checkNormalTransform() {
		if (normalTransform == null) {
			normalTransform = new Matrix3f(this.matrix);
			normalTransform.invert();
			normalTransform.transpose();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void push(PoseStack stack) {
		stack.pushPose();

		Vector3f trans = this.getTranslation();
		stack.translate(trans.x(), trans.y(), trans.z());

		stack.mulPose(this.getLeftRotation());

		Vector3f scale = this.getScale();
		stack.scale(scale.x(), scale.y(), scale.z());

		stack.mulPose(this.getRightRotation());
	}

	@Override
	public void transformPosition(Vector4f position) {
		position.transform(this.getMatrix());
	}

	@Override
	public Direction rotateTransform(Direction facing) {
		return Direction.rotate(getMatrix(), facing);
	}

	@Override
	public Transformation applyOrigin(Vector3f origin) {
		if (isIdentity()) return Transformation.identity();

		Matrix4f ret = this.getMatrix();
		Matrix4f tmp = Matrix4f.createTranslateMatrix(origin.x(), origin.y(), origin.z());
		ret.multiplyBackward(tmp);
		tmp.setIdentity();
		tmp.setTranslation(-origin.x(), -origin.y(), -origin.z());
		ret.multiply(tmp);
		return new Transformation(ret);
	}
}
