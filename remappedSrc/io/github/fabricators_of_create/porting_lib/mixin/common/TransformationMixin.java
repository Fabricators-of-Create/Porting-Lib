package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import io.github.fabricators_of_create.porting_lib.extensions.TransformationExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

@Mixin(AffineTransformation.class)
public abstract class TransformationMixin implements TransformationExtensions {
	@Shadow
	@Final
	private Matrix4f matrix;

	@Shadow
	public abstract Vec3f getTranslation();

	@Shadow
	public abstract Quaternion getRightRotation();

	@Shadow
	public abstract Vec3f getScale();

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
	public void push(MatrixStack stack) {
		stack.push();

		Vec3f trans = this.getTranslation();
		stack.translate(trans.getX(), trans.getY(), trans.getZ());

		stack.multiply(this.getLeftRotation());

		Vec3f scale = this.getScale();
		stack.scale(scale.getX(), scale.getY(), scale.getZ());

		stack.multiply(this.getRightRotation());
	}

	@Override
	public void transformPosition(Vector4f position) {
		position.transform(this.getMatrix());
	}

	@Override
	public Direction rotateTransform(Direction facing) {
		return Direction.transform(getMatrix(), facing);
	}

	@Override
	public AffineTransformation applyOrigin(Vec3f origin) {
		if (isIdentity()) return AffineTransformation.identity();

		Matrix4f ret = this.getMatrix();
		Matrix4f tmp = Matrix4f.translate(origin.getX(), origin.getY(), origin.getZ());
		ret.multiplyBackward(tmp);
		tmp.loadIdentity();
		tmp.setTranslation(-origin.getX(), -origin.getY(), -origin.getZ());
		ret.multiply(tmp);
		return new AffineTransformation(ret);
	}
}
