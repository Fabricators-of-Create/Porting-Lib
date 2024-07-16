package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;

@Environment(EnvType.CLIENT)
public interface RenderFrameEvent {
	Event<RenderFrameEvent> PRE = EventFactory.createArrayBacked(RenderFrameEvent.class, callbacks -> deltaTracker -> {
		for (final RenderFrameEvent event : callbacks)
			event.onRenderFrame(deltaTracker);
	});

	Event<RenderFrameEvent> POST = EventFactory.createArrayBacked(RenderFrameEvent.class, callbacks -> deltaTracker -> {
		for (final RenderFrameEvent event : callbacks)
			event.onRenderFrame(deltaTracker);
	});

	void onRenderFrame(DeltaTracker deltaTracker);
}
