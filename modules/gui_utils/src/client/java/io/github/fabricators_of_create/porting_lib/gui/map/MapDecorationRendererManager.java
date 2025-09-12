package io.github.fabricators_of_create.porting_lib.gui.map;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.MapDecorationTextureManager;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * In froge you register these via an event, but on fabric just register them directly via {@link MapDecorationRendererManager#register(MapDecorationType, IMapDecorationRenderer)}.
 */
public final class MapDecorationRendererManager {
	private static final Map<MapDecorationType, IMapDecorationRenderer> RENDERERS = new IdentityHashMap<>();

	private MapDecorationRendererManager() {}

	public static void register(MapDecorationType type, IMapDecorationRenderer renderer) {
		RENDERERS.put(type, renderer);
	}

	public static boolean render(
			MapDecoration decoration,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			MapItemSavedData mapData,
			MapDecorationTextureManager decorationTextures,
			boolean inItemFrame,
			int packedLight,
			int index) {
		IMapDecorationRenderer decorationRenderer = RENDERERS.get(decoration.type().value());
		if (decorationRenderer != null) {
			return decorationRenderer.render(decoration, poseStack, bufferSource, mapData, decorationTextures, inItemFrame, packedLight, index);
		}
		return false;
	}
}
