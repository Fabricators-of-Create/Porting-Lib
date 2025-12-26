package io.github.fabricators_of_create.porting_lib.client;

import io.github.fabricators_of_create.porting_lib.client.environment.CustomCloudsRenderer;
import io.github.fabricators_of_create.porting_lib.client.environment.CustomEnvironmentEffectsRendererManager;
import io.github.fabricators_of_create.porting_lib.client.environment.CustomSkyboxRenderer;
import io.github.fabricators_of_create.porting_lib.client.environment.CustomWeatherEffectRenderer;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.LevelRenderState;

import net.minecraft.world.phys.Vec3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class PortingLibBaseClient implements ClientModInitializer {
	private final Logger LOGGER = LoggerFactory.getLogger("porting_lib_client");

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FluidTextUtil.NUMBER_FORMAT);

		WorldRenderEvents.END_EXTRACTION.register(context -> {
			ClientLevel level = context.world();
			Vec3 cameraPos = context.camera().position();
			LevelRenderState renderState = context.worldState();
			renderState.setData(CustomWeatherEffectRenderer.KEY, CustomEnvironmentEffectsRendererManager.getCustomWeatherEffectRenderer(level, cameraPos));
			renderState.skyRenderState.setData(CustomSkyboxRenderer.KEY, CustomEnvironmentEffectsRendererManager.getCustomSkyboxRenderer(level, cameraPos));
			renderState.setData(CustomCloudsRenderer.KEY, CustomEnvironmentEffectsRendererManager.getCustomCloudsRenderer(level, cameraPos));
		});
	}
}
