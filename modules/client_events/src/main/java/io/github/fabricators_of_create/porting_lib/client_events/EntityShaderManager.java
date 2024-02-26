package io.github.fabricators_of_create.porting_lib.client_events;

import com.google.common.collect.ImmutableMap;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.RegisterEntitySpectatorShadersCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EntityShaderManager {
	private static final Map<EntityType<?>, ResourceLocation> SHADERS;

	/**
	 * Finds the path to the spectator mode shader used for the specified entity type, or null if none is registered.
	 */
	@Nullable
	public static ResourceLocation get(EntityType<?> entityType) {
		return SHADERS.get(entityType);
	}

	static {
		var shaders = new HashMap<EntityType<?>, ResourceLocation>();
		RegisterEntitySpectatorShadersCallback.EVENT.invoker().registerCustomShaders(shaders);
		SHADERS = ImmutableMap.copyOf(shaders);
	}
}
