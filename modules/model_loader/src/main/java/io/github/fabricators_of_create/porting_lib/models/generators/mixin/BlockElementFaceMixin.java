package io.github.fabricators_of_create.porting_lib.models.generators.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.models.materials.MaterialData;
import io.github.fabricators_of_create.porting_lib.models.generators.extensions.BlockElementFaceExtensions;
import net.minecraft.client.renderer.block.model.BlockElementFace;

@Mixin(BlockElementFace.class)
public class BlockElementFaceMixin implements BlockElementFaceExtensions {
	private MaterialData port_lib$material = MaterialData.DEFAULT;

	@Override
	public void port_lib$setRenderMaterial(MaterialData material) {
		this.port_lib$material = material;
	}

	@Override
	public MaterialData port_lib$getRenderMaterial() {
		return this.port_lib$material;
	}
}
