package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.EntityRenderersEvent;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
	@Shadow
	private Map<EntityType<?>, EntityRenderer<?>> renderers;

	@Shadow
	private Map<PlayerSkin.Model, EntityRenderer<? extends Player>> playerRenderers;

	@Inject(method = "onResourceManagerReload", at = @At("TAIL"))
	public void port_lib$resourceReload(ResourceManager resourceManager, CallbackInfo ci, @Local EntityRendererProvider.Context context) {
		EntityRenderersEvent.AddLayers event = new EntityRenderersEvent.AddLayers(renderers, playerRenderers, context);
		event.sendEvent();
	}
}

