package io.github.fabricators_of_create.porting_lib.renderable;


import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * A generic lookup for {@link RenderType} implementations that use the specified texture.
 */
@FunctionalInterface
public interface ITextureRenderTypeLookup {
	RenderType get(ResourceLocation name);
}
