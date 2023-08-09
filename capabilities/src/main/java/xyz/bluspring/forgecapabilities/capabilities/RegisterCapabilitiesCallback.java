package xyz.bluspring.forgecapabilities.capabilities;

import java.util.List;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RegisterCapabilitiesCallback {
	Event<RegisterCapabilities> EVENT = EventFactory.createArrayBacked(RegisterCapabilities.class, callbacks -> (capabilities) -> {
		for (RegisterCapabilities callback : callbacks) {
			callback.onRegisterCapability(capabilities);
		}
	});

	interface RegisterCapabilities {
		void onRegisterCapability(List<Class<?>> capabilities);
	}
}
