package io.github.fabricators_of_create.porting_lib.event.client;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

@Environment(EnvType.CLIENT)
public interface RegisterShadersCallback {
	Event<RegisterShadersCallback> EVENT = EventFactory.createArrayBacked(RegisterShadersCallback.class, callbacks -> (resourceManager, registry) -> {
		for(RegisterShadersCallback event : callbacks) {
			event.onShaderReload(resourceManager, registry);
		}
	});

	record ShaderRegistry(List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList) {
		public void registerShader(ShaderInstance shaderInstance, Consumer<ShaderInstance> onLoaded) {
			shaderList.add(Pair.of(shaderInstance, onLoaded));
		}
	}

	void onShaderReload(ResourceProvider resourceManager, ShaderRegistry registry) throws IOException;
}
