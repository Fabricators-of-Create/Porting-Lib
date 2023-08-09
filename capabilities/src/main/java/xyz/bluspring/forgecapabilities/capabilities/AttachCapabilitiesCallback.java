package xyz.bluspring.forgecapabilities.capabilities;

import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public interface AttachCapabilitiesCallback {
    Event<AttachCapabilities> EVENT = EventFactory.createArrayBacked(AttachCapabilities.class, callbacks -> (obj, capabilities, listeners) -> {
		for (AttachCapabilities callback : callbacks) {
			callback.onAttachCapability(obj, capabilities, listeners);
		}
	});

    interface AttachCapabilities {
        void onAttachCapability(ICapabilityProvider obj, Map<ResourceLocation, ICapabilityProvider> capabilities, List<Runnable> listeners);
    }
}
