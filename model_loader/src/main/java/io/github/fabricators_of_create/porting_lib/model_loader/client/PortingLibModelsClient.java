package io.github.fabricators_of_create.porting_lib.model_loader.client;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.RegisterShadersCallback;
import io.github.fabricators_of_create.porting_lib.model_loader.mixin.client.accessor.BlockModelAccessor;
import io.github.fabricators_of_create.porting_lib.model_loader.model.PortingLibRenderTypes;
import io.github.fabricators_of_create.porting_lib.model_loader.util.TransformationHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PortingLibModelsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RegisterShadersCallback.EVENT.register(PortingLibRenderTypes.Internal::initEntityTranslucentUnlitShader);
		NamedRenderTypeManager.init();
		BlockModelAccessor.setGSON(BlockModelAccessor.port_lib$GSON().newBuilder().registerTypeAdapter(Transformation.class, new TransformationHelper.Deserializer()).create());
	}
}
