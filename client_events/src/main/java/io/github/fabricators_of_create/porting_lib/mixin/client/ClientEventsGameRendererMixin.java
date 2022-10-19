package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.event.client.RegisterShadersCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class ClientEventsGameRendererMixin {

	@Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownShaders()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void port_lib$registerShaders(ResourceProvider provider, CallbackInfo ci, List<Program> list, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderRegistry) {
		try {
			RegisterShadersCallback.EVENT.invoker().onShaderReload(provider, new RegisterShadersCallback.ShaderRegistry(shaderRegistry));
		} catch (IOException e) {
			throw new RuntimeException("[Porting Lib] failed to reload modded shaders", e);
		}
	}
}
