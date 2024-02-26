package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.ColorResolver;

import org.jetbrains.annotations.ApiStatus;

public interface RegisterColorResolversCallback {
	Event<RegisterColorResolversCallback> EVENT = EventFactory.createArrayBacked(RegisterColorResolversCallback.class, callbacks -> registrationHelper -> {
		for (RegisterColorResolversCallback e : callbacks)
			e.onColorResolverCreation(registrationHelper);
	});

	class RegistrationHelper {
		private final ImmutableList.Builder<ColorResolver> builder = ImmutableList.builder();
		public void register(ColorResolver resolver) {
			this.builder.add(resolver);
		}

		@ApiStatus.Internal
		public ImmutableList<ColorResolver> build() {
			return builder.build();
		}
	}

	void onColorResolverCreation(RegistrationHelper registrationHelper);
}
