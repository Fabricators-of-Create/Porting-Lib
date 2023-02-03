package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class ModelEvents {
	public static final Event<ModelsBakedCallback> MODELS_BAKED = EventFactory.createArrayBacked(ModelsBakedCallback.class, callbacks -> (manager, models, loader) -> {
		for (ModelsBakedCallback callback : callbacks) {
			callback.onModelsBaked(manager, models, loader);
		}
	});

	public static final Event<ModelsModifyBakingResult> MODIFY_BAKING_RESULT = EventFactory.createArrayBacked(ModelsModifyBakingResult.class, callbacks -> (models, modelBakery) -> {
		for (ModelsModifyBakingResult callback : callbacks) {
			callback.onModifyBakingResult(models, modelBakery);
		}
	});

	@FunctionalInterface
	public interface ModelsBakedCallback {

		void onModelsBaked(ModelManager manager, Map<ResourceLocation, BakedModel> models, ModelBakery loader);
	}

	public interface ModelsModifyBakingResult {

		void onModifyBakingResult(Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery);
	}
}
