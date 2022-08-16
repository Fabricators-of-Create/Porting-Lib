package io.github.fabricators_of_create.porting_lib.event.client;

import com.google.common.collect.ImmutableMap;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.level.block.SkullBlock;

public interface CreateSkullModelsCallback {
	Event<CreateSkullModelsCallback> EVENT = EventFactory.createArrayBacked(CreateSkullModelsCallback.class, callbacks -> (builder, entityModelSet) -> {
		for (CreateSkullModelsCallback e : callbacks)
			e.onSkullModelsCreated(builder, entityModelSet);
	});

	void onSkullModelsCreated(ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder, EntityModelSet entityModelSet);
}
