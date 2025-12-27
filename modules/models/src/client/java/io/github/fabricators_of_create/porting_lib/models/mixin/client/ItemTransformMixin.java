package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import io.github.fabricators_of_create.porting_lib.models.injects.ItemTransformInjection;
import net.minecraft.client.renderer.block.model.ItemTransform;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemTransform.class)
public class ItemTransformMixin implements ItemTransformInjection {
	public Vector3f rightRotation;

	@Override
	public Vector3f port_lib$getRightRotation() {
		return this.rightRotation;
	}

	@Override
	public void port_lib$setRightRotation(Vector3f rightRotation) {
		this.rightRotation = rightRotation;
	}
}
