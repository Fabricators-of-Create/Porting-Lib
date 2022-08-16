package io.github.fabricators_of_create.porting_lib.client;

import io.github.fabricators_of_create.porting_lib.event.client.RegisterShadersCallback;
import io.github.fabricators_of_create.porting_lib.model.PortingLibRenderTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Porting_libClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RegisterShadersCallback.EVENT.register(PortingLibRenderTypes.Internal::initEntityTranslucentUnlitShader);
	}
}
