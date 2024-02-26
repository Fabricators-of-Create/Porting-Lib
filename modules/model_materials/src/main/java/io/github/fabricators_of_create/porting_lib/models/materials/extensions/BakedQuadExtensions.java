package io.github.fabricators_of_create.porting_lib.models.materials.extensions;

import io.github.fabricators_of_create.porting_lib.models.materials.MaterialData;

public interface BakedQuadExtensions {
	void port_lib$setRenderMaterial(MaterialData material);

	MaterialData port_lib$getRenderMaterial();
}
