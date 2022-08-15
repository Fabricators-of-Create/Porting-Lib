package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public interface MinecraftTailCallback {
	Event<MinecraftTailCallback> EVENT = EventFactory.createArrayBacked(MinecraftTailCallback.class, callbacks -> mc -> {
		for (MinecraftTailCallback callback : callbacks) {
			callback.onMinecraftTail(mc);
		}
	});

	void onMinecraftTail(Minecraft mc);
}
