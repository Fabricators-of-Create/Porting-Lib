package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.crafting.RecipeManager;

@Environment(EnvType.CLIENT)
public interface RecipesUpdatedCallback {
  Event<RecipesUpdatedCallback> EVENT = EventFactory.createArrayBacked(RecipesUpdatedCallback.class, callbacks -> manager -> {
    for(RecipesUpdatedCallback event : callbacks)
      event.onRecipesUpdated(manager);
  });

  void onRecipesUpdated(RecipeManager manager);
}
