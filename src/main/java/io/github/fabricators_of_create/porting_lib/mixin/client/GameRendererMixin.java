package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.event.RegisterShadersCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownShaders()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$registerShaders(ResourceManager manager, CallbackInfo ci, List<Program> list, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderRegistry) {
		try {
			RegisterShadersCallback.EVENT.invoker().onShaderReload(manager, new RegisterShadersCallback.ShaderRegistry(shaderRegistry));
		} catch (IOException e) {
			throw new RuntimeException("[Porting Lib] failed to reload modded shaders", e);
		}
	}
}
