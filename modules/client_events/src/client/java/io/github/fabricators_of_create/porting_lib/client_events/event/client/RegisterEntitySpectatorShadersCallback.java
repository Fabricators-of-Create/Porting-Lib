package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

/**
 * Allows users to register custom shaders to be used when the player spectates a certain kind of entity.
 * Vanilla examples of this are the green effect for creepers and the invert effect for endermen.
 */
public interface RegisterEntitySpectatorShadersCallback {
	Event<RegisterEntitySpectatorShadersCallback> EVENT = EventFactory.createArrayBacked(RegisterEntitySpectatorShadersCallback.class, callbacks -> shaders -> {
		for (RegisterEntitySpectatorShadersCallback e : callbacks)
			e.registerCustomShaders(shaders);
	});

	void registerCustomShaders(Map<EntityType<?>, ResourceLocation> shaders);
}
