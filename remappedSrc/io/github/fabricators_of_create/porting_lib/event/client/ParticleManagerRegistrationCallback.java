package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public interface ParticleManagerRegistrationCallback {
	Event<ParticleManagerRegistrationCallback> EVENT = EventFactory.createArrayBacked(ParticleManagerRegistrationCallback.class, callbacks -> () -> {
		for (ParticleManagerRegistrationCallback callback : callbacks) {
			callback.onParticleManagerRegistration();
		}
	});

	void onParticleManagerRegistration();
}
