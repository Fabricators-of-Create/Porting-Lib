package io.github.fabricators_of_create.porting_lib.models.extensions;

import net.minecraft.client.renderer.RenderType;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface BlockModelExtensions {
	void port_lib$setRenderType(RenderType type);
}
