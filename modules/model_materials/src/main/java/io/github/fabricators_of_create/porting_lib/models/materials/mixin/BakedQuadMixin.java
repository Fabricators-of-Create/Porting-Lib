package io.github.fabricators_of_create.porting_lib.models.materials.mixin;

import io.github.fabricators_of_create.porting_lib.models.materials.MaterialData;
import io.github.fabricators_of_create.porting_lib.models.materials.extensions.BakedQuadExtensions;
import net.minecraft.client.renderer.block.model.BakedQuad;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedQuad.class)
public class BakedQuadMixin implements BakedQuadExtensions {
	private MaterialData port_lib$renderMaterial;

	@Override
	public void port_lib$setRenderMaterial(MaterialData material) {
		this.port_lib$renderMaterial = material;
	}

	@Override
	public MaterialData port_lib$getRenderMaterial() {
		return this.port_lib$renderMaterial;
	}
}
