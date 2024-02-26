package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.event.client.EntityAddedLayerCallback;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
	@Shadow
	private Map<EntityType<?>, EntityRenderer<?>> renderers;

	@Shadow
	private Map<String, EntityRenderer<? extends Player>> playerRenderers;

	@Inject(method = "onResourceManagerReload", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$resourceReload(ResourceManager resourceManager, CallbackInfo ci, EntityRendererProvider.Context context) {
		EntityAddedLayerCallback.EVENT.invoker().addLayers(renderers, playerRenderers);
	}
}
