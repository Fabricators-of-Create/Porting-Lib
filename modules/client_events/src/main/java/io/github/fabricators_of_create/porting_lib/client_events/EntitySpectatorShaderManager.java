package io.github.fabricators_of_create.porting_lib.client_events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EntitySpectatorShaderManager {
	private static final Map<EntityType<?>, ResourceLocation> SHADERS = new HashMap<>();

	/**
	 * Finds the path to the spectator mode shader used for the specified entity type, or null if none is registered.
	 */
	@Nullable
	public static ResourceLocation get(EntityType<?> entityType) {
		return SHADERS.get(entityType);
	}

	public static void register(EntityType<?> entityType, ResourceLocation location) {
		SHADERS.put(entityType, location);
	}
}
