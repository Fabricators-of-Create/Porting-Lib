package io.github.fabricators_of_create.porting_lib.event.client;

import com.google.common.collect.ImmutableMap;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;

public interface CreateSkullModelsCallback {
	Event<CreateSkullModelsCallback> EVENT = EventFactory.createArrayBacked(CreateSkullModelsCallback.class, callbacks -> (builder, entityModelSet) -> {
		for (CreateSkullModelsCallback e : callbacks)
			e.onSkullModelsCreated(builder, entityModelSet);
	});

	void onSkullModelsCreated(ImmutableMap.Builder<SkullBlock.SkullType, SkullBlockEntityModel> builder, EntityModelLoader entityModelSet);
}
