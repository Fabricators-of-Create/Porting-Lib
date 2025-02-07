package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Map;

@FunctionalInterface
public interface EntityAddedLayerCallback {

	Event<EntityAddedLayerCallback> EVENT = EventFactory.createArrayBacked(EntityAddedLayerCallback.class, callbacks -> (renderers, skinMap) -> {
		for (EntityAddedLayerCallback event : callbacks) {
			event.addLayers(renderers, skinMap);
		}
	});

	void addLayers(final Map<EntityType<?>, EntityRenderer<?>> renderers, final Map<String, EntityRenderer<? extends PlayerEntity>> skinMap);

}
