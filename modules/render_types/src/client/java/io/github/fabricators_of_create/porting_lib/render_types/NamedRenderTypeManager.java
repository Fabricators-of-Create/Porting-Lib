package io.github.fabricators_of_create.porting_lib.render_types;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for named {@link RenderType render types}.
 * <p>
 * Provides a lookup.
 */
public final class NamedRenderTypeManager {
	private static Map<ResourceLocation, RenderTypeGroup> RENDER_TYPES = new HashMap<>();

	/**
	 * Finds the {@link RenderTypeGroup} for a given name, or the {@link RenderTypeGroup#EMPTY empty group} if not found.
	 */
	public static RenderTypeGroup get(ResourceLocation name) {
		return RENDER_TYPES.getOrDefault(name, RenderTypeGroup.EMPTY);
	}

	public static void register(ResourceLocation key, RenderType blockRenderType, RenderType entityRenderType) {
		RENDER_TYPES.put(key, new RenderTypeGroup(blockRenderType, entityRenderType));
	}

	/**
	 * Pre-registers vanilla render types.
	 */
	static {
		register(ResourceLocation.withDefaultNamespace("solid"), RenderType.solid(), PortingLibRenderTypes.ITEM_LAYERED_SOLID.get());
		register(ResourceLocation.withDefaultNamespace("cutout"), RenderType.cutout(), PortingLibRenderTypes.ITEM_LAYERED_CUTOUT.get());
		// Generally entity/item rendering shouldn't use mipmaps, so cutout_mipped has them off by default. To enforce them, use cutout_mipped_all.
		register(ResourceLocation.withDefaultNamespace("cutout_mipped"), RenderType.cutoutMipped(), PortingLibRenderTypes.ITEM_LAYERED_CUTOUT.get());
		register(ResourceLocation.withDefaultNamespace("cutout_mipped_all"), RenderType.cutoutMipped(), PortingLibRenderTypes.ITEM_LAYERED_CUTOUT_MIPPED.get());
		register(ResourceLocation.withDefaultNamespace("translucent"), RenderType.translucent(), PortingLibRenderTypes.ITEM_LAYERED_TRANSLUCENT.get());
		register(ResourceLocation.withDefaultNamespace("tripwire"), RenderType.tripwire(), PortingLibRenderTypes.ITEM_LAYERED_TRANSLUCENT.get());
	}

	private NamedRenderTypeManager() {}
}
