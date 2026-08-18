package io.github.fabricators_of_create.porting_lib.models.geometry.mixin.client;

import java.util.Map;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.models.ExtraFaceData;
import io.github.fabricators_of_create.porting_lib.models.geometry.extensions.BlockElementExtension;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.core.Direction;

@Mixin(BlockElement.class)
public class BlockElementMixin implements BlockElementExtension {

	private ExtraFaceData port_lib$faceData = ExtraFaceData.DEFAULT;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initParentsForFaces(Vector3f from, Vector3f to, Map<Direction, BlockElementFace> faces, BlockElementRotation rotation, boolean shade, CallbackInfo ci) {
		for (BlockElementFace value : faces.values()) {
			value.port_lib$parent().setValue((BlockElement) (Object) this);
		}
	}

	@Override
	public ExtraFaceData port_lib$getFaceData() {
		return this.port_lib$faceData;
	}

	@Override
	public void port_lib$setFaceData(ExtraFaceData faceData) {
		this.port_lib$faceData = java.util.Objects.requireNonNull(faceData);
	}
}
