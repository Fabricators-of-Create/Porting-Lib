package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;

/**
 * @deprecated move to {@link FieldOfViewEvents}
 */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface FOVModifierCallback {
  Event<FOVModifierCallback> EVENT = EventFactory.createArrayBacked(FOVModifierCallback.class, callbacks -> (player, fov) -> {
    for(FOVModifierCallback e : callbacks) {
      float newFov = e.getNewFOV(player, fov);
      if(newFov != fov)
        return newFov;
    }
    return fov;
  });

  Event<PartialFOV> PARTIAL_FOV = EventFactory.createArrayBacked(PartialFOV.class, callbacks -> ((renderer, camera, partialTick, fov) -> {
	  for(PartialFOV e : callbacks) {
		  double newFov = e.getNewFOV(renderer, camera, partialTick, fov);
		  if(newFov != fov)
			  return newFov;
	  }
	  return fov;
  }));

  @FunctionalInterface
  interface PartialFOV {
	  double getNewFOV(GameRenderer renderer, Camera camera, double partialTick, double fov);
  }

  float getNewFOV(Player entity, float fov);
}
