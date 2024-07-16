package io.github.fabricators_of_create.porting_lib.config;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ConfigRegistry {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Adds a {@link ModConfig} with the given type and spec. An empty config spec will be ignored and a debug line will
	 * be logged.
	 *
	 * @param type       The type of config
	 * @param configSpec A config spec
	 */
	public static void registerConfig(String modId, ModConfig.Type type, IConfigSpec configSpec) {
		if (configSpec.isEmpty()) {
			// This handles the case where a mod tries to register a config, without any options configured inside it.
			LOGGER.debug("Attempted to register an empty config for type {} on mod {}", type, modId);
			return;
		}

		ConfigTracker.INSTANCE.registerConfig(type, configSpec, FabricLoader.getInstance().getModContainer(modId).orElseThrow());
	}

	/**
	 * Adds a {@link ModConfig} with the given type, spec, and overridden file name. An empty config spec will be
	 * ignored and a debug line will be logged.
	 *
	 * @param type       The type of config
	 * @param configSpec A config spec
	 */
	public static void registerConfig(String modId, ModConfig.Type type, IConfigSpec configSpec, String fileName) {
		if (configSpec.isEmpty()) {
			// This handles the case where a mod tries to register a config, without any options configured inside it.
			LOGGER.debug("Attempted to register an empty config for type {} on mod {} using file name {}", type, modId, fileName);
			return;
		}

		ConfigTracker.INSTANCE.registerConfig(type, configSpec, FabricLoader.getInstance().getModContainer(modId).orElseThrow(), fileName);
	}

	// This is kinda hacky but meh we can come up with a better solution later

	public static Event<ModConfigEvent.Loading.Callback> loading(String modId) {
		return ConfigTracker.INSTANCE.loadingEventsByMod.computeIfAbsent(modId, key -> {
			Event<ModConfigEvent.Loading.Callback> c = EventFactory.createArrayBacked(ModConfigEvent.Loading.Callback.class, callbacks -> event -> {
				for (ModConfigEvent.Loading.Callback callback : callbacks)
					callback.onLoading(event);
			});
			ModConfigEvent.Loading.EVENT.register(event -> {
				if (event.getConfig().getModId().equals(key))
					c.invoker().onLoading(event);
			});
			return c;
		});
	}

	public static Event<ModConfigEvent.Reloading.Callback> reloading(String modId) {
		return ConfigTracker.INSTANCE.reloadingEventsByMod.computeIfAbsent(modId, key -> {
			Event<ModConfigEvent.Reloading.Callback> c = EventFactory.createArrayBacked(ModConfigEvent.Reloading.Callback.class, callbacks -> event -> {
				for (ModConfigEvent.Reloading.Callback callback : callbacks)
					callback.onReloading(event);
			});
			ModConfigEvent.Reloading.EVENT.register(event -> {
				if (event.getConfig().getModId().equals(key))
					c.invoker().onReloading(event);
			});
			return c;
		});
	}

	public static Event<ModConfigEvent.Unloading.Callback> unloading(String modId) {
		return ConfigTracker.INSTANCE.unloadingEventsByMod.computeIfAbsent(modId, key -> {
			Event<ModConfigEvent.Unloading.Callback> c = EventFactory.createArrayBacked(ModConfigEvent.Unloading.Callback.class, callbacks -> event -> {
				for (ModConfigEvent.Unloading.Callback callback : callbacks)
					callback.onUnloading(event);
			});
			ModConfigEvent.Unloading.EVENT.register(event -> {
				if (event.getConfig().getModId().equals(key))
					c.invoker().onUnloading(event);
			});
			return c;
		});
	}
}
