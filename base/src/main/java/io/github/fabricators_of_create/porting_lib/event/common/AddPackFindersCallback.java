package io.github.fabricators_of_create.porting_lib.event.common;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.repository.RepositorySource;

public interface AddPackFindersCallback {
	Event<AddPackFindersCallback> EVENT = EventFactory.createArrayBacked(AddPackFindersCallback.class, callbacks -> (sources) -> {
		for (AddPackFindersCallback e : callbacks) {
			e.addPack(sources);
		}
	});

	void addPack(Consumer<RepositorySource> sources);
}
