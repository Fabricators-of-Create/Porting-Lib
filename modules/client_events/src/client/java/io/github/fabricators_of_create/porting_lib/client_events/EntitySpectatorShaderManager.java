package io.github.fabricators_of_create.porting_lib.client_events;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for entity spectator mode shaders.
 * <p>
 * Provides a lookup.
 */
public class EntitySpectatorShaderManager {
	private static final Map<EntityType<?>, Identifier> SHADERS = new HashMap<>();

	/**
	 * Finds the path to the spectator mode shader used for the specified entity type, or null if none is registered.
	 */
	@Nullable
	public static Identifier get(EntityType<?> entityType) {
		return SHADERS.get(entityType);
	}

	public static void register(EntityType<?> entityType, Identifier shader) {
		SHADERS.put(entityType, shader);
	}
}
