package io.github.fabricators_of_create.porting_lib.config;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public abstract class ModConfigEvent extends BaseEvent {
	private final ModConfig config;

	ModConfigEvent(final ModConfig config) {
		this.config = config;
	}

	public ModConfig getConfig() {
		return config;
	}

	/**
	 * Fired during mod and server loading, depending on {@link ModConfig.Type} of config file.
	 * Any Config objects associated with this will be valid and can be queried directly.
	 */
	public static class Loading extends ModConfigEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onLoading(event);
		});

		public Loading(final ModConfig config) {
			super(config);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onLoading(this);
		}

		public interface Callback {
			void onLoading(Loading event);
		}
	}

	/**
	 * Fired when the configuration is changed. This can be caused by a change to the config
	 * from a UI or from editing the file itself. IMPORTANT: this can fire at any time
	 * and may not even be on the server or client threads. Ensure you properly synchronize
	 * any resultant changes.
	 */
	public static class Reloading extends ModConfigEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onReloading(event);
		});

		public Reloading(final ModConfig config) {
			super(config);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onReloading(this);
		}

		public interface Callback {
			void onReloading(Reloading event);
		}
	}

	/**
	 * Fired when a config is unloaded. This only happens when the server closes, which is
	 * probably only really relevant on the client, to reset internal mod state when the
	 * server goes away, though it will fire on the dedicated server as well.
	 * The config file will be saved after this event has fired.
	 */
	public static class Unloading extends ModConfigEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onUnloading(event);
		});

		public Unloading(final ModConfig config) {
			super(config);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onUnloading(this);
		}

		public interface Callback {
			void onUnloading(Unloading event);
		}
	}
}
