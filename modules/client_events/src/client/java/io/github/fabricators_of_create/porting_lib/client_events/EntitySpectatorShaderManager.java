package io.github.fabricators_of_create.porting_lib.client_events;

import com.google.common.collect.ImmutableMap;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.RegisterEntitySpectatorShadersCallback;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceLocation;
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
	private static final Map<EntityType<?>, Identifier> SHADERS;

	/**
	 * Finds the path to the spectator mode shader used for the specified entity type, or null if none is registered.
	 */
	@Nullable
	public static Identifier get(EntityType<?> entityType) {
		return SHADERS.get(entityType);
	}

	static {
		var shaders = new HashMap<EntityType<?>, Identifier>();
		RegisterEntitySpectatorShadersCallback.EVENT.invoker().registerCustomShaders(shaders);
		SHADERS = ImmutableMap.copyOf(shaders);
	}
}
