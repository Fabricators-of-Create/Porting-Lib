package io.github.fabricators_of_create.porting_lib.config;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface ConfigEvents {
	Event<ConfigEvents> LOADING = EventFactory.createArrayBacked(ConfigEvents.class, configEvents -> config -> {
		for (ConfigEvents e : configEvents)
			e.onModConfigEvent(config);
	});

	Event<ConfigEvents> RELOADING = EventFactory.createArrayBacked(ConfigEvents.class, configEvents -> config -> {
		for (ConfigEvents e : configEvents)
			e.onModConfigEvent(config);
	});

	Event<ConfigEvents> UNLOADING = EventFactory.createArrayBacked(ConfigEvents.class, configEvents -> config -> {
		for (ConfigEvents e : configEvents)
			e.onModConfigEvent(config);
	});

	void onModConfigEvent(ModConfig config);
}
