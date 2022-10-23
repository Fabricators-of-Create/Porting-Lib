package io.github.fabricators_of_create.porting_lib.model_loader.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class PortingRenderTypes {
	/**
	 * @return A RenderType fit for translucent item/entity rendering, but with diffuse lighting disabled
	 * so that fullbright quads look correct.
	 * @param sortingEnabled If false, depth sorting will not be performed.
	 */
	public static RenderType getUnlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled) {
		return (sortingEnabled ? UNLIT_TRANSLUCENT_SORTED : UNLIT_TRANSLUCENT_UNSORTED).apply(textureLocation);
	}

	public static Function<ResourceLocation, RenderType> UNLIT_TRANSLUCENT_SORTED = Util.memoize(tex -> unlitTranslucent(tex, true));
	public static Function<ResourceLocation, RenderType> UNLIT_TRANSLUCENT_UNSORTED = Util.memoize(tex -> unlitTranslucent(tex, false));
	private static RenderType unlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled)
	{
//		RenderType.CompositeState renderState = RenderType.CompositeState.builder()
//				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_UNLIT_SHADER)
//				.setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, false))
//				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
//				.setCullState(NO_CULL)
//				.setLightmapState(LIGHTMAP)
//				.setOverlayState(OVERLAY)
//				.createCompositeState(true);
//		return create("forge_entity_unlit_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, sortingEnabled, renderState);
		return null;
	}
}
