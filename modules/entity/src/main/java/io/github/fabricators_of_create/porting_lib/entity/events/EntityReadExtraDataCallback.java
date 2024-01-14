package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public interface EntityReadExtraDataCallback {
	Event<EntityReadExtraDataCallback> EVENT = EventFactory.createArrayBacked(EntityReadExtraDataCallback.class, callbacks -> (entity, data) -> {
		for (EntityReadExtraDataCallback callback : callbacks) {
			callback.onLoad(entity, data);
		}
	});

	void onLoad(Entity entity, @Nullable CompoundTag extraData);
}
