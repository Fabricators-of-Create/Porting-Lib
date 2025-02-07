package io.github.fabricators_of_create.porting_lib.event.client;

import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface ModelsBakedCallback {
	Event<ModelsBakedCallback> EVENT = EventFactory.createArrayBacked(ModelsBakedCallback.class, callbacks -> (manager, models, loader) -> {
		for (ModelsBakedCallback callback : callbacks) {
			callback.onModelsBaked(manager, models, loader);
		}
	});

	void onModelsBaked(BakedModelManager manager, Map<Identifier, BakedModel> models, ModelLoader loader);
}
