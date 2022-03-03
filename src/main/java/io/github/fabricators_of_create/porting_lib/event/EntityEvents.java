package io.github.fabricators_of_create.porting_lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public class EntityEvents {

  public static Event<Remove> ON_REMOVE = EventFactory.createArrayBacked(Remove.class, callbacks -> ((entity, reason) -> {
    for(Remove e : callbacks)
      e.onRemove(entity, reason);
  }));

  public static final Event<EyeHeight> EYE_HEIGHT = EventFactory.createArrayBacked(EyeHeight.class, callbacks -> (entity) -> {
    for (EyeHeight callback : callbacks) {
      return callback.onEntitySize(entity);
    }

    return -1;
  });

  @FunctionalInterface
  public interface Remove {
    void onRemove(Entity entity, Entity.RemovalReason reason);
  }

  @FunctionalInterface
  public interface EyeHeight {
    int onEntitySize(Entity entity);
  }
}
