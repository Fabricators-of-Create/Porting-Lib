package io.github.fabricators_of_create.porting_lib.models.geometry.mixin.client;

import io.github.fabricators_of_create.porting_lib.models.ExtraFaceData;
import io.github.fabricators_of_create.porting_lib.models.geometry.extensions.BlockElementExt;
import net.minecraft.client.renderer.block.model.BlockElement;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockElement.class)
public class BlockElementMixin implements BlockElementExt {

	private ExtraFaceData port_lib$faceData;

	@Override
	public ExtraFaceData port_lib$getFaceData() {
		return this.port_lib$faceData;
	}

	@Override
	public void port_lib$setFaceData(ExtraFaceData faceData) {
		this.port_lib$faceData = java.util.Objects.requireNonNull(faceData);
	}
}
