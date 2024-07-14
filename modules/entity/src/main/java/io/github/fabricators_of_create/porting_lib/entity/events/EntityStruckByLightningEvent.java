package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

/**
 * EntityStruckByLightningEvent is fired when an Entity is about to be struck by lightening.<br>
 * This event is fired whenever an EntityLightningBolt is updated to strike an Entity in
 * {@link LightningBolt#tick()} via {@link EntityHooks#onEntityStruckByLightning(Entity, LightningBolt)}.<br>
 * <br>
 * {@link #lightning} contains the instance of EntityLightningBolt attempting to strike an entity.<br>
 * <br>
 * This event is {@link CancellableEvent}.<br>
 * If this event is canceled, the Entity is not struck by the lightening.<br>
 **/
public class EntityStruckByLightningEvent extends EntityEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onStruckByLightning(event);
	});

	private final LightningBolt lightning;

	public EntityStruckByLightningEvent(Entity entity, LightningBolt lightning) {
		super(entity);
		this.lightning = lightning;
	}

	public LightningBolt getLightning() {
		return lightning;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onStruckByLightning(this);
	}

	public interface Callback {
		void onStruckByLightning(EntityStruckByLightningEvent event);
	}
}
