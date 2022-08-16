package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import java.util.Map;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface EntityAddedLayerCallback {

	Event<EntityAddedLayerCallback> EVENT = EventFactory.createArrayBacked(EntityAddedLayerCallback.class, callbacks -> (renderers, skinMap) -> {
		for (EntityAddedLayerCallback event : callbacks) {
			event.addLayers(renderers, skinMap);
		}
	});

	void addLayers(final Map<EntityType<?>, EntityRenderer<?>> renderers, final Map<String, EntityRenderer<? extends Player>> skinMap);

}
