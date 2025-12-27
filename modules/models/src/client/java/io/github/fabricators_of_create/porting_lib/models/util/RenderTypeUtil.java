package io.github.fabricators_of_create.porting_lib.models.util;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public final class RenderTypeUtil {
	private static final ImmutableMap<ResourceLocation, RenderType> RENDER_TYPES;

	@Nullable
	public static RenderType get(ResourceLocation name) {
		return RENDER_TYPES.getOrDefault(name, null);
	}

	static {
		var renderTypes = new HashMap<ResourceLocation, RenderType>();
		renderTypes.put(ResourceLocation.withDefaultNamespace("solid"), RenderType.solid());
		renderTypes.put(ResourceLocation.withDefaultNamespace("cutout"), RenderType.cutout());
		// Generally entity/item rendering shouldn't use mipmaps, so cutout_mipped has them off by default. To enforce them, use cutout_mipped_all.
		renderTypes.put(ResourceLocation.withDefaultNamespace("cutout_mipped"), RenderType.cutoutMipped());
		renderTypes.put(ResourceLocation.withDefaultNamespace("cutout_mipped_all"), RenderType.cutoutMipped());
		renderTypes.put(ResourceLocation.withDefaultNamespace("translucent"), RenderType.translucent());
		renderTypes.put(ResourceLocation.withDefaultNamespace("tripwire"), RenderType.tripwire());
		RENDER_TYPES = ImmutableMap.copyOf(renderTypes);
	}
}
