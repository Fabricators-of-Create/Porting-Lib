package io.github.fabricators_of_create.porting_lib.extensions.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.ItemTransformExtensions;
import net.minecraft.client.renderer.block.model.ItemTransform;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemTransform.class)
public class ItemTransformMixin implements ItemTransformExtensions {
	public Vector3f rightRotation;

	@Override
	public Vector3f getRightRotation() {
		return this.rightRotation;
	}

	@Override
	public void setRightRotation(Vector3f rightRotation) {
		this.rightRotation = rightRotation;
	}
}
